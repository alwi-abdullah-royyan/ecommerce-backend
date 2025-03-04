package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.request.OrderRequest;
import com.alwi.ecommerce.dto.response.OrderResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.exception.UnauthorizedException;
import com.alwi.ecommerce.exception.ValidationException;
import com.alwi.ecommerce.model.*;
import com.alwi.ecommerce.repository.*;
import com.alwi.ecommerce.util.OrderStatusUtil.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.alwi.ecommerce.util.OrderStatusUtil.getAllowedStatusTransitions;
import static com.alwi.ecommerce.util.OrderStatusUtil.willCauseProductReturn;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderHistoryService orderHistoryService;

    public Page<OrderResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> order = orderRepository.findAll(pageable);
            if (order.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return order.map(OrderService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    public List<OrderStatus> getAllowedStatusesForOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found: " + id));

        OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());
        return getAllowedStatusTransitions(currentStatus);
    }
    public OrderResponse findById(Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Order order;
        if (user.getRole().equals("CUSTOMER")) {
            order = orderRepository.findByUserAndId(user, id)
                    .orElseThrow(() -> new DataNotFoundException("Order not found: " + id));
        } else if(user.getRole().equals("ADMIN")) {
            order = orderRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Order not found: " + id));
        } else{
            throw new UnauthorizedException("You are not authorized to view this order");
        }
        return convertToResponse(order);
    }

    private User getCurrentUser(Authentication authentication) {
        UserDetails auth = (UserDetails) authentication.getPrincipal();
        return userRepository.findUserByUsername(auth.getUsername())
                .orElseThrow(() -> new DataNotFoundException("Current user not found"));
    }
    public Page<OrderResponse> getOrderByUser(int page, int size, Authentication authentication){
        User user = getCurrentUser(authentication);
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> order = orderRepository.findByUser( user,pageable );
        return order.map(OrderService::convertToResponse);
    }
    public Page<OrderResponse> getOrderByUserAndStatus(int page, int size, String status, Authentication authentication){
        User user = getCurrentUser(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> order = orderRepository.findByUserAndStatus(user, status, pageable);

        return order.map(OrderService::convertToResponse);
    }
    public Page<OrderResponse> getOrderByStatus (int page, int size, String status, Authentication authentication){
        User user = getCurrentUser(authentication);
        if (user.getRole().equals("CUSTOMER")) {
            throw new UnauthorizedException("You are not authorized to view this order");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> order = orderRepository.findByStatus(status, pageable);

        return order.map(OrderService::convertToResponse);
    }
//=====================METHODS NEEDED FOR CREATE ORDER==========================
    @Transactional
    public OrderResponse create(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<Cart> carts = getCheckedCarts(user);

            validateProductStock(carts);

            Order order = buildOrder(user, carts);
            Order savedOrder = orderRepository.save(order);
            createHistory(order);
            processOrderItems(savedOrder, carts);

            return convertToResponse(savedOrder);
        } catch (DataNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order", e);
        }
    }

    private List<Cart> getCheckedCarts(User user) {
        List<Cart> carts = cartRepository.findByCheckedAndUser(true, user);
        if (carts.isEmpty()) {
            throw new DataNotFoundException("No checked carts available");
        }
        return carts;
    }

    private void validateProductStock(List<Cart> carts) {
        for (Cart cart : carts) {
            int availableQty = cart.getProduct().getQty();
            int requestedQty = cart.getQty();
            if (requestedQty > availableQty) {
                throw new IllegalArgumentException("Insufficient stock for product: " + cart.getProduct().getName()
                        + ". Available: " + availableQty + ", Requested: " + requestedQty);
            }
        }
    }

    private Order buildOrder(User user, List<Cart> carts) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING.toString());

        BigDecimal totalPrice = carts.stream()
                .map(cart -> cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
        return order;
    }

    private void processOrderItems(Order order, List<Cart> carts) {
        for (Cart cart : carts) {
            createOrderItem(order, cart);
            updateProductQty(cart);
            cartRepository.delete(cart); // Clear the cart
        }
    }

    private void createOrderItem(Order order, Cart cart) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(cart.getProduct());
        orderItem.setQty(cart.getQty());
        orderItem.setSubtotal(cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQty())));
        orderItemRepository.save(orderItem);
    }

    private void updateProductQty(Cart cart) {
        int newQty = cart.getProduct().getQty() - cart.getQty();
        productService.updateQty(cart.getProduct().getId(), newQty);
    }

    private void createHistory(Order order){
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrder(order);
        orderHistory.setStatus(order.getStatus());
        orderHistoryService.create(order);
    }
//==================END OF ORDER METHODS====================================

//====================UPDATE ORDER METHODS==========================
    public OrderResponse updateStatus(Long id, String status) {
        OrderStatus newStatus = parseOrderStatus(status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found: " + id));
        OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());

        // Validate if the new status is allowed
        List<OrderStatus> allowedStatuses = getAllowedStatusTransitions(currentStatus);
        if (!allowedStatuses.contains(newStatus)) {
            throw new ValidationException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
        order.setStatus(newStatus.name());
        Order updatedOrder = orderRepository.save(order);
        createHistory(order);
        //returning item
        if(willCauseProductReturn(newStatus)){
            List<OrderItem> orderItem = orderItemRepository.findByOrder(updatedOrder);

            for (OrderItem orderItems : orderItem) {
                int newQty = orderItems.getProduct().getQty() + orderItems.getQty();
                productService.updateQty(orderItems.getProduct().getId(), newQty);
            }
        }

        return convertToResponse(updatedOrder);
    }

    private OrderStatus parseOrderStatus(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid status: " + status);
        }
    }
//====================END OF UPDATE ORDER METHODS==========================

//====================DELETE ORDER METHODS==========================
    @Transactional
    public void delete(Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Order not found: " + id));

            OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());

            if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.COMPLETED) {
                throw new DataNotFoundException("Order cannot be deleted as it is already " + currentStatus);
            }
            if (isReturnableStatus(currentStatus)) {
                List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                for (OrderItem orderItem : orderItems) {
                    returnProduct(orderItem);
                }
            }
            order.getOrderItems().clear();
            orderRepository.deleteById(id);

        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting order", e);
        }
    }

    private boolean isReturnableStatus(OrderStatus status) {
        return Arrays.asList(OrderStatus.PENDING, OrderStatus.PROCESSING).contains(status);
    }

    private void returnProduct(OrderItem orderItem) {
        Product product = orderItem.getProduct();
        int newQty = product.getQty() + orderItem.getQty();
        productService.updateQty(product.getId(), newQty);
    }
//====================END OF DELETE ORDER METHODS==========================


    public static OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setUsername(order.getUser().getUsername());
        response.setStatus(order.getStatus());
        response.setTotalPrice(order.getTotalPrice());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}

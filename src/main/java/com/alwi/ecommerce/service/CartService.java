package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.request.CartRequest;
import com.alwi.ecommerce.dto.response.CartResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.model.Cart;
import com.alwi.ecommerce.model.Product;
import com.alwi.ecommerce.model.User;
import com.alwi.ecommerce.repository.CartRepository;
import com.alwi.ecommerce.repository.ProductRepository;
import com.alwi.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    public Page<CartResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Cart> carts = cartRepository.findAll(pageable);
            if (carts.isEmpty()){
                throw new DataNotFoundException("Cart not found");
            }
            return carts.map(CartService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    public Page<CartResponse> findByUserId(UUID id, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Cart> carts = cartRepository.findByUserId(id, pageable);
            return carts.map(CartService::convertToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Error finding cart",e);
        }
    }
    @Transactional
    public CartResponse createOrUpdateCart(CartRequest cartRequest) {
        try {
            System.out.println("Service called");

            User user = userRepository.findUserByUsername(cartRequest.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("User not found: " + cartRequest.getUsername()));
            System.out.println("User found: " + user.getUsername());

            Product product = productRepository.findById(cartRequest.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found: " + cartRequest.getProductId()));
            System.out.println("Product found: " + product.getName());

            Optional<Cart> existingCartOpt = cartRepository.findByUserAndProduct(user, product);
            Cart cart;

            if (existingCartOpt.isPresent()) {
                System.out.println("Existing cart found");
                cart = existingCartOpt.get();
                cart.setTotal(cart.getTotal() + cartRequest.getTotal());

                // 🛠️ Handle deletion if total is 0 or less
                if (cart.getTotal() <= 0) {
                    System.out.println("Total is 0 or less, deleting cart...");
                    delete(cart.getId());
                    return null;
                }
            } else {
                System.out.println("Creating a new cart");
                cart = new Cart();
                cart.setTotal(cartRequest.getTotal());
                cart.setUser(user); // 🆕 Properly set user
                cart.setProduct(product); // 🆕 Properly set product
            }

            Cart savedCart = cartRepository.save(cart);
            return convertToResponse(savedCart);

        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error creating or updating cart", e);
        }
    }


    //delete
    @Transactional
    public Boolean delete(Long id) {
        try {
            if (!cartRepository.existsById(id)) {
                return false;
            }
            cartRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting cart",e);
        }
    }
    public static CartResponse convertToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setTotal(cart.getTotal());
        //user
        response.setUserId(cart.getUser().getId());
        response.setUserName(cart.getUser().getUsername());
        //product
        response.setProductId(cart.getProduct().getId());
        response.setProductName(cart.getProduct().getName());
        response.setProductPrice(cart.getProduct().getPrice());
        response.setProductImage(cart.getProduct().getImage());
        return response;
    }
}

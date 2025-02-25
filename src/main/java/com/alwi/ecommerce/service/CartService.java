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

import java.math.BigDecimal;
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
            User user = userRepository.findUserByUsername(cartRequest.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("User not found: " + cartRequest.getUsername()));

            Product product = productRepository.findById(cartRequest.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found: " + cartRequest.getProductId()));

            Optional<Cart> existingCartOpt = cartRepository.findByUserAndProduct(user, product);
            Cart cart;

            if (existingCartOpt.isPresent()) {
                cart = existingCartOpt.get();
                cart.setQty(cart.getQty() + cartRequest.getQty());

                // If quantity is zero or less, remove the cart item
                if (cart.getQty() <= 0) {
                    delete(cart.getId());
                    return null;
                }

            } else {
                System.out.println("Creating a new cart");
                cart = new Cart();
                cart.setQty(cartRequest.getQty());
                cart.setUser(user);
                cart.setProduct(product);
            }

            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(cart.getQty()));
            cart.setPrice(totalPrice);

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
        response.setQty(cart.getQty());
        response.setPrice(cart.getPrice());
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

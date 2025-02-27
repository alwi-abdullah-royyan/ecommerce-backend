package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.request.CartRequest;
import com.alwi.ecommerce.dto.response.CartResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.exception.UnauthorizedException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    public Page<CartResponse> findByUserId(Authentication authentication, int page, int size) {
        try {
            UserDetails auth = (UserDetails) authentication.getPrincipal();
            User currentUser = userRepository.findUserByUsername(auth.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("Current user not found"));
            Pageable pageable = PageRequest.of(page, size);
            Page<Cart> carts = cartRepository.findByUserId(currentUser.getId(), pageable);
            return carts.map(CartService::convertToResponse);
        } catch (DataNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Error finding cart",e);
        }
    }
    @Transactional
    public CartResponse createOrUpdateCart(Authentication authentication,CartRequest cartRequest) {
        try {
            UserDetails auth = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findUserByUsername(auth.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("Current user not found"));

            Product product = productRepository.findById(cartRequest.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found: " + cartRequest.getProductId()));

            Optional<Cart> existingCartOpt = cartRepository.findByUserAndProduct(user, product);
            Cart cart;

            if (existingCartOpt.isPresent()) {
                cart = existingCartOpt.get();
                cart.setQty(cartRequest.getQty());

                if (cart.getQty() <= 0) {
                    delete(authentication, cart.getId());
                    return null;
                }
            } else {
                cart = new Cart();
                cart.setQty(cartRequest.getQty());
                cart.setUser(user);
                cart.setProduct(product);
                cart.setChecked(true);
                cart.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cart.getQty())));
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
    public void delete(Authentication authentication,Long id) {
        try {
            UserDetails auth = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findUserByUsername(auth.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("Current user not found"));
            Cart cart = cartRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Cart not found: " + id));
            if (user.getId() != cart.getUser().getId()) {
                throw new UnauthorizedException("You are not authorized to delete this cart");
            }
            cartRepository.deleteById(id);
        } catch(DataNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Error deleting cart",e);
        }
    }
    @Transactional
    public CartResponse checkCart(Authentication authentication,Long id,CartRequest cartRequest) {
        try {
            UserDetails auth = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findUserByUsername(auth.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("Current user not found"));
            Cart cart = cartRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Cart not found: " + id));
            if (user.getId() != cart.getUser().getId()) {
                throw new UnauthorizedException("You are not authorized to check this cart");
            }
            System.out.println(cartRequest);
            cart.setChecked(cartRequest.getChecked());
            Cart savedCart = cartRepository.save(cart);
            return convertToResponse(savedCart);
        } catch(UnauthorizedException | DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error checking cart",e);
        }
    }
    public static CartResponse convertToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setQty(cart.getQty());
        response.setPrice(cart.getPrice());
        response.setChecked(cart.getChecked());
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

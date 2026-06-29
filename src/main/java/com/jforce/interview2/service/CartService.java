package com.jforce.interview2.service;

import com.jforce.interview2.dto.CartItemRequest;
import com.jforce.interview2.dto.CartItemResponse;
import com.jforce.interview2.dto.CartResponse;
import com.jforce.interview2.model.*;
import com.jforce.interview2.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final ProductRepo productRepo;
    private final InventoryRepo inventoryRepo;
    private final UserRepo userRepo;

    private User getLoggedInUser(){
        String email = SecurityContextHolder.getContext().getAuthentication()
                .getName();
        return userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not found"));
    }

    private Cart getOrCreateCart(User user){
        return cartRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepo.save(cart);
                });
    }

    private CartItemResponse toItemDTO(CartItem cartItem) {
        BigDecimal subTotal = cartItem.getPriceAtAddition()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getPriceAtAddition(),
                cartItem.getQuantity(),
                subTotal
        );
    }

    private CartResponse toCartDto(Cart cart) {
        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(this::toItemDTO)
                .toList();

        // total amount = sum of all subtotals
        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // total items = sum of all quantities
        int totalItems = cart.getItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return new CartResponse(
                cart.getId(),
                items,
                total,
                totalItems
        );
    }

    //user endpoints
    public CartResponse getCart(){
        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);

        return toCartDto(cart);
    }


    @Transactional
    public CartResponse addToCart(CartItemRequest request){
        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepo.findById(request.productId())
                .orElseThrow(()  -> new RuntimeException("Product not found"));
        if(!product.isEnabled()) {
            throw new RuntimeException("Product is not active");
        }
        Inventory inventory = inventoryRepo.findByProductId(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if(inventory.getQuantity() < request.quantity()) {
            throw new RuntimeException("Not enough products available");
        }
        Optional<CartItem> existingItem = cartItemRepo
                .findByCartIdAndProductId(cart.getId(), request.productId());

        if (existingItem.isPresent()) {
            // item exists → update quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.quantity();

            // validate total quantity against inventory
            if (inventory.getQuantity() < newQuantity) {
                throw new RuntimeException(
                        "Insufficient stock for total quantity. Available: "
                                + inventory.getQuantity()
                                + " Requested total: " + newQuantity
                );
            }

            item.setQuantity(newQuantity);
            cartItemRepo.save(item);
        } else {
            // item not in cart → add new
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .priceAtAddition(product.getPrice()) // snapshot price
                    .build();
            cartItemRepo.save(newItem);
        }

        // reload cart to get updated items
        Cart updatedCart = cartRepo.findById(cart.getId())
                .orElseThrow();
        return toCartDto(updatedCart);

    }


    @Transactional
    public CartResponse updateItem(Integer cartItemId, Integer quantity) {
        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found: " + cartItemId));

        // make sure this cart item belongs to logged in user
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (quantity <= 0) {
            // if quantity is 0 or less → remove item
            cartItemRepo.delete(item);
        } else {
            // validate inventory
            Inventory inventory = inventoryRepo
                    .findByProductId(item.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Inventory not found"));

            if (inventory.getQuantity() < quantity) {
                throw new RuntimeException(
                        "Insufficient stock. Available: "
                                + inventory.getQuantity()
                );
            }

            item.setQuantity(quantity);
            cartItemRepo.save(item);
        }

        Cart updatedCart = cartRepo.findById(cart.getId()).orElseThrow();
        return toCartDto(updatedCart);
    }

    @Transactional
    public CartResponse removeItem(Integer cartItemId) {
        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepo.findById(cartItemId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found: " + cartItemId));

        // make sure this cart item belongs to logged in user
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        cartItemRepo.delete(item);

        Cart updatedCart = cartRepo.findById(cart.getId()).orElseThrow();
        return toCartDto(updatedCart);
    }

    // clear entire cart
    @Transactional
    public void clearCart() {
        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);
        cartItemRepo.deleteByCartId(cart.getId());
    }

    // validate cart before checkout
    // called internally by OrderService before placing order
    public void validateCart(Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Cart is empty. Add items before checkout.");
        }

        // check every item against current inventory
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();

            if (!product.isEnabled()) {
                throw new RuntimeException(
                        "Product no longer available: " + product.getName()
                                + ". Please remove it from cart."
                );
            }

            Inventory inventory = inventoryRepo
                    .findByProductId(product.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Inventory not found for: "
                                    + product.getName()));

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for: " + product.getName()
                                + ". Available: " + inventory.getQuantity()
                                + " In cart: " + item.getQuantity()
                );
            }
        }
    }

}

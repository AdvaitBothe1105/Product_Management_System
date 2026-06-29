package com.jforce.interview2.controller;

import com.jforce.interview2.dto.CartItemRequest;
import com.jforce.interview2.dto.CartResponse;
import com.jforce.interview2.service.CartService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(){
        return cartService.getCart();
    }

    @PostMapping("/items")
    public CartResponse addToCart(@RequestBody CartItemRequest cartItemRequest) {
        return cartService.addToCart(cartItemRequest);
    }

    @PatchMapping("/items/{id}")
    public CartResponse updateItem(
            @PathVariable Integer id,
            @RequestParam @Min(0) Integer quantity
    ) {
        return cartService.updateItem(id,quantity);
    }
    
    @DeleteMapping("/items/{id}")
    public CartResponse removeItem(@PathVariable Integer id) {
        return cartService.removeItem(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(){
        cartService.clearCart();
    }


}

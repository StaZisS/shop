package com.example.shop.cart;

import com.example.shop.core.cart.CartRepository;
import com.example.shop.core.cart.CartRepositoryImpl;
import com.example.shop.core.cart.CartService;
import com.example.shop.public_interface.cart.CartDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CartServiceUnitTest {
    private CartService cartService;
    private CartRepository cartRepository;

    @BeforeEach
    public void setup() {
        cartRepository = mock(CartRepositoryImpl.class);

        cartService = new CartService(cartRepository);
    }

    @Test
    public void invalidCount() {
        var cartDto = new CartDto(
                UUID.randomUUID(),
                "",
                -1
        );

        assertThrows(ExceptionInApplication.class, () -> cartService.setCountProduct(cartDto));
    }
}

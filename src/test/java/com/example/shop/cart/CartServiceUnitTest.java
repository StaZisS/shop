package com.example.shop.cart;

import com.example.shop.core.cart.CartRepository;
import com.example.shop.core.cart.CartRepositoryImpl;
import com.example.shop.core.cart.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void test() {

    }
}

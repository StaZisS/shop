package com.example.shop.order;

import com.example.shop.core.cart.CartRepository;
import com.example.shop.core.cart.CartRepositoryImpl;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.client.repository.ClientRepositoryImpl;
import com.example.shop.core.order.repository.OrderRepository;
import com.example.shop.core.order.repository.OrderRepositoryImpl;
import com.example.shop.core.order.service.OrderService;
import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.core.product.repository.ProductRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

public class OrderServiceUnitTest {
    private OrderService orderService;
    private OrderRepository orderRepository;
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;

    @BeforeEach
    public void setup() {
        orderRepository = mock(OrderRepositoryImpl.class);
        cartRepository = mock(CartRepositoryImpl.class);
        productRepository = mock(ProductRepositoryImpl.class);
        clientRepository = mock(ClientRepositoryImpl.class);

        orderService = new OrderService(orderRepository, cartRepository, productRepository, clientRepository);
    }


}

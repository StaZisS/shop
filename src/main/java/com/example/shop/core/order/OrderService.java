package com.example.shop.core.order;

import com.example.shop.core.cart.CartEntity;
import com.example.shop.core.cart.CartRepository;
import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import com.example.shop.public_interface.order.CreateOrderDto;
import com.example.shop.public_interface.order.OrderDetailsDto;
import com.example.shop.public_interface.order.OrderSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public void createOrder(CreateOrderDto dto) {
        var listCartEntity = cartRepository.getListCartEntity(dto.productsCodeInCart(), dto.clientId());

        var totalPrice = getTotalPrice(listCartEntity);
        var orderEntity = getDefaultOrderEntity(totalPrice, dto.clientId(), dto.addressDeliveryCode());
        var productsInOrder = getProducts(orderEntity.orderId(), listCartEntity);

        orderRepository.createOrder(orderEntity, productsInOrder);

        cartRepository.deletePositions(dto.productsCodeInCart(), dto.clientId());
    }

    public void cancelOrder(UUID orderId) {

    }

    public OrderDetailsDto getStatusOrder(UUID orderId, UUID clientId) {
        return null;
    }

    public List<OrderSummaryDto> getOrders(UUID clientId) {
        return null;
    }

    private OrderEntity getDefaultOrderEntity(BigDecimal totalPrice, UUID clientId, String deliveryAddressCode) {
        return new OrderEntity(
                UUID.randomUUID(),
                clientId,
                deliveryAddressCode,
                "",
                totalPrice,
                DeliveryStatus.PROCESSING,
                OffsetDateTime.now(),
                null
        );
    }

    private BigDecimal getTotalPrice(List<CartEntity> productInOrder) {
        return productInOrder.stream()
                .map(product -> {
                    var productPrice = productRepository.getCommonProduct(product.productCode())
                            .orElseThrow(() -> new ExceptionInApplication("Продукт не найден", ExceptionType.NOT_FOUND))
                            .price();
                    return productPrice.multiply(BigDecimal.valueOf(product.countProduct()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ProductInOrderEntity> getProducts(UUID orderId, List<CartEntity> productInOrder) {
        return productInOrder.stream()
                .map(product -> new ProductInOrderEntity(
                        orderId,
                        product.productCode(),
                        product.countProduct()
                ))
                .toList();
    }
}

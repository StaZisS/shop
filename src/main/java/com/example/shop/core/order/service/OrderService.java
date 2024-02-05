package com.example.shop.core.order.service;

import com.example.shop.core.cart.repository.CartEntity;
import com.example.shop.core.cart.repository.CartRepository;
import com.example.shop.core.client.repository.ClientRepository;
import com.example.shop.core.order.repository.DeliveryStatus;
import com.example.shop.core.order.repository.OrderEntity;
import com.example.shop.core.order.repository.OrderRepository;
import com.example.shop.core.order.repository.ProductInOrderEntity;
import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import com.example.shop.public_interface.order.CreateOrderDto;
import com.example.shop.public_interface.order.OrderDetailsDto;
import com.example.shop.public_interface.order.OrderSummaryDto;
import com.example.shop.public_interface.order.ProductDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final List<DeliveryStatus> UN_CANCELABLE_STATUSES = List.of(DeliveryStatus.CANCELED, DeliveryStatus.DELIVERED);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    public UUID createOrder(CreateOrderDto dto) {
        var productsInCart = cartRepository.getListCartEntity(dto.productsCodeInCart(), dto.clientId());

        var totalPrice = getTotalPrice(productsInCart);
        var orderEntity = getDefaultOrderEntity(totalPrice, dto.clientId(), dto.addressDeliveryCode());
        var productsInOrder = getProducts(orderEntity.orderId(), productsInCart);

        orderRepository.createOrder(orderEntity, productsInOrder);

        cartRepository.deletePositions(dto.productsCodeInCart(), dto.clientId());

        return orderEntity.orderId();
    }

    public void cancelOrder(UUID orderId, UUID clientId) {
        validateCanCancelableOrder(orderId);

        orderRepository.cancelOrder(orderId);
    }

    public OrderDetailsDto getOrderDetails(UUID orderId, UUID clientId) {
        var orderEntity = orderRepository.getOrder(orderId)
                .orElseThrow(() -> new ExceptionInApplication("Заказ не найден", ExceptionType.NOT_FOUND));
        var productsInOrder = orderRepository.getProductsInOrder(orderId);

        var productsDetailDto = getProductsDetailDto(productsInOrder);

        return OrderMapper.mapEntityToDto(orderEntity, productsDetailDto);
    }

    public List<OrderSummaryDto> getOrders(UUID clientId) {
        var orders = orderRepository.getOrders(clientId);

        return orders.stream()
                .map(OrderMapper::mapEntityToDto)
                .toList();
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

    private void validateCanCancelableOrder(UUID orderId) {
        var orderEntity = orderRepository.getOrder(orderId)
                .orElseThrow(() -> new ExceptionInApplication("Заказ не найден", ExceptionType.NOT_FOUND));

        var isNotCancelableStatus = UN_CANCELABLE_STATUSES.contains(orderEntity.status());
        if (isNotCancelableStatus) {
            var exceptionMessage = String.format("Заказ со статусом %s невозможно отсенить", orderEntity.status().name());
            throw new ExceptionInApplication(exceptionMessage, ExceptionType.INVALID);
        }
    }

    private List<ProductDetailsDto> getProductsDetailDto(List<ProductInOrderEntity> products) {
        return products.stream()
                .map(productInOrder -> {
                    var productEntity = productRepository.getCommonProduct(productInOrder.productCode())
                            .orElseThrow(() -> new ExceptionInApplication("Продукт не найден" ,ExceptionType.NOT_FOUND));

                    return OrderMapper.mapEntityToDto(productEntity, productInOrder.countProduct());
                })
                .toList();
    }
}

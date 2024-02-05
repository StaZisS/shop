package com.example.shop.core.order.repository;

import com.example.shop.public_.tables.records.OrdersRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.shop.public_.tables.Orders.ORDERS;
import static com.example.shop.public_.tables.ProductInOrder.PRODUCT_IN_ORDER;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final DSLContext create;
    private final OrderEntityMapper orderEntityMapper = new OrderEntityMapper();
    private final ProductInOrderEntityMapper productInOrderEntityMapper = new ProductInOrderEntityMapper();

    @Override
    public void createOrder(OrderEntity entity, List<ProductInOrderEntity> productsInOrder) {
        create.transaction(configuration -> {
            final DSLContext ctx = DSL.using(configuration);

            ctx.insertInto(ORDERS)
                    .set(ORDERS.ORDER_ID, entity.orderId())
                    .set(ORDERS.CLIENT_ID, entity.clientId())
                    .set(ORDERS.ADDRESS_DELIVERY_CODE, entity.addressDeliveryCode())
                    .set(ORDERS.ADDRESS_DELIVERY, entity.addressDelivery())
                    .set(ORDERS.TOTAL_PRICE, entity.totalPrice())
                    .set(ORDERS.STATUS, entity.status().name())
                    .set(ORDERS.CREATION_DATE, entity.creationDate())
                    .set(ORDERS.TRACK_NUMBER, entity.trackNumber())
                    .execute();

            createProductsInOrder(productsInOrder, ctx);
        });
    }

    @Override
    public Optional<OrderEntity> getOrder(UUID orderId) {
        return create.selectFrom(ORDERS)
                .where(ORDERS.ORDER_ID.eq(orderId))
                .fetchOptional(orderEntityMapper);
    }

    @Override
    public List<ProductInOrderEntity> getProductsInOrder(UUID orderId) {
        return create.selectFrom(PRODUCT_IN_ORDER)
                .where(PRODUCT_IN_ORDER.ORDER_ID.eq(orderId))
                .fetch(productInOrderEntityMapper);
    }

    @Override
    public void cancelOrder(UUID orderId) {
        create.update(ORDERS)
                .set(ORDERS.STATUS, DeliveryStatus.CANCELED.name())
                .where(ORDERS.ORDER_ID.eq(orderId))
                .execute();
    }

    @Override
    public List<OrderEntity> getOrders(UUID clientId) {
        return create.selectFrom(ORDERS)
                .where(ORDERS.CLIENT_ID.eq(clientId))
                .fetch(orderEntityMapper);
    }

    @Override
    public List<OrderEntity> getAllOrdersWithProduct(UUID clientId, String productCode) {
        return create.select(ORDERS)
                .from(ORDERS)
                .join(PRODUCT_IN_ORDER)
                .on(PRODUCT_IN_ORDER.ORDER_ID.eq(ORDERS.ORDER_ID))
                .and(PRODUCT_IN_ORDER.PRODUCT_CODE.eq(productCode))
                .where(ORDERS.CLIENT_ID.eq(clientId))
                .fetch(record1 -> new OrderEntity(
                        record1.value1().getOrderId(),
                        record1.value1().getClientId(),
                        record1.value1().getAddressDeliveryCode(),
                        record1.value1().getAddressDelivery(),
                        record1.value1().getTotalPrice(),
                        DeliveryStatus.getDeliveryStatusByName(record1.value1().getStatus()),
                        record1.value1().getCreationDate(),
                        record1.value1().getTrackNumber()
                ));
    }

    private void createProductsInOrder(List<ProductInOrderEntity> productsInOrder, DSLContext localCtx) {
        for (var productInOrder : productsInOrder) {
            createProductInOrder(productInOrder, localCtx);
        }
    }

    private void createProductInOrder(ProductInOrderEntity entity, DSLContext localCtx) {
        localCtx.insertInto(PRODUCT_IN_ORDER)
                .set(PRODUCT_IN_ORDER.ORDER_ID, entity.orderId())
                .set(PRODUCT_IN_ORDER.PRODUCT_CODE, entity.productCode())
                .set(PRODUCT_IN_ORDER.COUNT_PRODUCT, entity.countProduct())
                .execute();
    }
}

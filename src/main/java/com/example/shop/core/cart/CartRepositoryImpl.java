package com.example.shop.core.cart;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.example.shop.public_.tables.Cart.CART;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final DSLContext create;

    @Override
    public int getCountProduct(UUID clientId, String productCode) {
        return getCountProduct(clientId, productCode, create);
    }

    @Override
    public void setCountProduct(CartEntity entity) {
        create.transaction(configuration -> {
            final DSLContext ctx = DSL.using(configuration);

            var countProduct = getCountProduct(entity.clientId(), entity.productCode(), ctx);

            if (countProduct == 0) {
                createPosition(entity, ctx);
            } else {
                updatePosition(entity, ctx);
            }
        });
    }

    @Override
    public void deleteProductPosition(UUID clientId, String productCode) {
        deletePosition(clientId, productCode, create);
    }

    private void updatePosition(CartEntity entity, DSLContext localCtx) {
        localCtx.update(CART)
                .set(CART.COUNT_PRODUCT, entity.countProduct())
                .where(CART.CLIENT_ID.eq(entity.clientId()).and(CART.PRODUCT_CODE.eq(entity.productCode())))
                .execute();
    }

    private void createPosition(CartEntity entity, DSLContext localCtx) {
        localCtx.insertInto(CART)
                .set(CART.CLIENT_ID, entity.clientId())
                .set(CART.PRODUCT_CODE, entity.productCode())
                .set(CART.COUNT_PRODUCT, entity.countProduct())
                .execute();
    }

    private void deletePosition(UUID clientId, String productCode, DSLContext localCtx) {
        localCtx.delete(CART)
                .where(CART.CLIENT_ID.eq(clientId).and(CART.PRODUCT_CODE.eq(productCode)))
                .execute();
    }

    private int getCountProduct(UUID clientId, String productCode, DSLContext localCtx) {
        return localCtx.selectFrom(CART)
                .where(CART.CLIENT_ID.eq(clientId).and(CART.PRODUCT_CODE.eq(productCode)))
                .fetchOptional(CART.COUNT_PRODUCT)
                .orElse(0);
    }
}

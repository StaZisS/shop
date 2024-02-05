package com.example.shop.core.favorites.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.example.shop.public_.tables.Favorites.FAVORITES;

@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements  FavoriteRepository {
    private final DSLContext create;

    @Override
    public void switchFavoriteStatus(UUID clientId, String productCode) {
        create.transaction(configuration -> {
            final DSLContext ctx = DSL.using(configuration);

            final boolean isNotFavorite = !isFavoriteProduct(clientId, productCode, ctx);

            setFavorite(clientId, productCode, isNotFavorite, ctx);
        });
    }

    @Override
    public boolean isFavorite(UUID clientId, String productCode) {
        return isFavoriteProduct(clientId, productCode, create);
    }

    private boolean isFavoriteProduct(UUID clientId, String productCode, DSLContext localCtx) {
        return localCtx.selectFrom(FAVORITES)
                .where(FAVORITES.CLIENT_ID.eq(clientId))
                .and(FAVORITES.PRODUCT_CODE.eq(productCode))
                .fetchOptional()
                .isPresent();
    }

    private void setFavorite(UUID clientId, String productCode, boolean value, DSLContext localCtx) {
        System.out.println(value);
        if (value) {
            insertInFavorite(clientId, productCode, localCtx);
        } else {
            deleteFromFavorite(clientId, productCode, localCtx);
        }
    }
    
    private void insertInFavorite(UUID clientId, String productCode, DSLContext localCtx) {
        localCtx.insertInto(FAVORITES)
                .set(FAVORITES.CLIENT_ID, clientId)
                .set(FAVORITES.PRODUCT_CODE, productCode)
                .execute();
    }
    
    private void deleteFromFavorite(UUID clientId, String productCode, DSLContext localCtx) {
        localCtx.delete(FAVORITES)
                .where(FAVORITES.CLIENT_ID.eq(clientId))
                .and(FAVORITES.PRODUCT_CODE.eq(productCode))
                .execute();
    }
}

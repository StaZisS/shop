package com.example.shop.core.product.repository;

import com.example.shop.publicInterface.FilterDto;
import com.example.shop.publicInterface.ProductCommonDto;
import com.example.shop.public_.tables.Product;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;
import org.jooq.DSLContext;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final DSLContext create;

    private final ProductCommonMapper productCommonMapper = new ProductCommonMapper();

    //TODO: проверка на то что строка не пуста
    @Override
    public List<ProductCommonEntity> getCommonProducts(FilterDto filterDto) {
        return create.selectFrom(Product.PRODUCT)
                .where(Product.PRODUCT.NORMALIZED_NAME.contains(filterDto.productName()))
                .fetchStream()
                .map(productCommonMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public void addProduct(ProductCommonEntity entity) {
        create.insertInto(Product.PRODUCT)
                .set(Product.PRODUCT.CODE, entity.code())
                .set(Product.PRODUCT.NAME, entity.name())
                .set(Product.PRODUCT.NORMALIZED_NAME, entity.normalizeName())
                .set(Product.PRODUCT.PRICE, entity.price())
                .set(Product.PRODUCT.RATING, entity.rating())
                .set(Product.PRODUCT.ORDER_QUANTITY, entity.orderQuantity())
                .set(Product.PRODUCT.ADDITIONAL_INFO, JSONB.valueOf(entity.additionalInfo()))
                .execute();
    }
}

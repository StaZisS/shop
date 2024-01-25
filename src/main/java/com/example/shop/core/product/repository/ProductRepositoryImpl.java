package com.example.shop.core.product.repository;

import com.example.shop.core.util.QueryTool;
import com.example.shop.public_interface.FilterDto;
import com.example.shop.public_interface.PaginationDto;
import com.example.shop.public_interface.ProductPageDto;
import com.example.shop.public_.tables.Product;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.stream.Collectors;

import static com.example.shop.public_.Routines.getProductsPaged;
import static org.jooq.impl.DSL.count;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final DSLContext create;

    private final ProductCommonMapper productCommonMapper = new ProductCommonMapper();

    @Override
    public ProductPageDto getCommonProductsPage(FilterDto filterDto) {
        var pagination = filterDto.pagination();
        var normalizedName = QueryTool.getNormalizedString(filterDto.productName());

        if (normalizedName.isEmpty()) {
            normalizedName = null;
        }

        var productsWithSize = create.selectFrom(getProductsPaged(pagination.pageSize(), pagination.pageNumber(), normalizedName, filterDto.sortType().name()))
                .fetch().collect(Collectors.toList());

        if (productsWithSize.isEmpty()) {
            return new ProductPageDto(
                    Collections.emptyList(),
                    new PaginationDto(
                            0,
                            0,
                            pagination.pageNumber()
                    )
            );
        }


        var paginationInfo = new PaginationDto(
                productsWithSize.size(),
                productsWithSize.getFirst().getCountPage(),
                pagination.pageNumber()
        );

        var products = productsWithSize.stream()
                .map(productCommonMapper::map)
                .collect(Collectors.toList());

        return new ProductPageDto(
                products,
                paginationInfo
        );
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

    @Override
    public int getCountProducts(String query) {
        var normalizedName = QueryTool.getNormalizedString(query);
        if (normalizedName.isEmpty()) {
            return create.select(count())
                    .from(Product.PRODUCT)
                    .execute();
        }
        return create.select(count())
                .from(Product.PRODUCT)
                .where(Product.PRODUCT.NORMALIZED_NAME.contains(normalizedName))
                .execute();
    }
}

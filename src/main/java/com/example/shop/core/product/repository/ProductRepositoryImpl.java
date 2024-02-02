package com.example.shop.core.product.repository;

import com.example.shop.core.util.QueryTool;
import com.example.shop.public_.tables.records.ProductRecord;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.PaginationDto;
import com.example.shop.public_interface.product.ProductPageDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;
import org.jooq.Name;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Repository;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.shop.public_.Routines.getProductsPaged;
import static org.jooq.impl.DSL.count;
import static com.example.shop.public_.tables.Product.PRODUCT;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final DSLContext create;

    private final ProductCommonMapperToDto productCommonMapperToDto = new ProductCommonMapperToDto();
    private final ProductCommonMapperToEntity productCommonMapperToEntity = new ProductCommonMapperToEntity();

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
                .map(productCommonMapperToDto)
                .collect(Collectors.toList());

        return new ProductPageDto(
                products,
                paginationInfo
        );
    }

    @Override
    public Optional<ProductCommonEntity> addProduct(ProductCommonEntity entity) {
        return create.insertInto(PRODUCT)
                .set(PRODUCT.CODE, entity.code())
                .set(PRODUCT.STORE_ID, entity.storeId())
                .set(PRODUCT.NAME, entity.name())
                .set(PRODUCT.NORMALIZED_NAME, entity.normalizeName())
                .set(PRODUCT.PRICE, entity.price())
                .set(PRODUCT.RATING, entity.rating())
                .set(PRODUCT.ORDER_QUANTITY, entity.orderQuantity())
                .set(PRODUCT.ADDITIONAL_INFO, JSONB.valueOf(entity.additionalInfo()))
                .returningResult(PRODUCT)
                .fetchOptional(productCommonMapperToEntity);
    }

    @Override
    public int getCountProducts(@NonNull String query) {
        var normalizedName = QueryTool.getNormalizedString(query);
        if (normalizedName.isEmpty()) {
            return create.select(count())
                    .from(PRODUCT)
                    .execute();
        }
        return create.select(count())
                .from(PRODUCT)
                .where(PRODUCT.NORMALIZED_NAME.contains(normalizedName))
                .execute();
    }

    @Override
    public Optional<ProductCommonEntity> getCommonProduct(String productCode) {
        return create.selectFrom(PRODUCT)
                .where(PRODUCT.CODE.eq(productCode))
                .fetchOptional(productRecord -> new ProductCommonEntity(
                        productRecord.getCode(),
                        productRecord.getStoreId(),
                        Collections.emptyList(),
                        productRecord.getName(),
                        productRecord.getNormalizedName(),
                        productRecord.getPrice(),
                        productRecord.getRating(),
                        productRecord.getOrderQuantity(),
                        productRecord.getAdditionalInfo().data()
                ));
    }
}

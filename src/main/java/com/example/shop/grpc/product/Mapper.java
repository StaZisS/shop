package com.example.shop.grpc.product;

import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.ProductPageDto;
import com.example.shop.public_interface.product.SortType;
import product.CommonProduct;
import product.GetProductPageRequest;
import product.GetProductPageResponse;
import product.PaginationInfo;

public class Mapper {
    public static FilterDto mapRequestToDto(GetProductPageRequest request) {
        var pagination = new FilterDto.PaginationProperty(
                request.getPageSize(),
                request.getPageNumber()
        );
        return new FilterDto(
                request.getProductName(),
                SortType.getSortTypeByName(request.getSortType()),
                pagination
        );
    }

    public static GetProductPageResponse mapDtoToResponse(ProductPageDto dto) {
        var pagination = PaginationInfo.newBuilder()
                .setPageSize(dto.paginationDto().pageSize())
                .setCurrentPage(dto.paginationDto().currentPage())
                .setPageCount(dto.paginationDto().pageCount())
                .build();
        var listProduct = dto.products().stream()
                .map(product -> CommonProduct.newBuilder()
                                .addAllImagesUrl(product.imagesUrl())
                                .setCode(product.code())
                                .setName(product.name())
                                .setPrice(product.price().toString())
                                .setRating(product.rating())
                                .setOrderQuantity(product.orderQuantity())
                                .build()
                )
                .toList();
        return GetProductPageResponse.newBuilder()
                .addAllProducts(listProduct)
                .setPagination(pagination)
                .build();
    }
}

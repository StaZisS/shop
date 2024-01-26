package com.example.shop.public_interface.product;

import java.util.List;

public record ProductPageDto (
        List<ProductCommonDto> products,
        PaginationDto paginationDto
){
}

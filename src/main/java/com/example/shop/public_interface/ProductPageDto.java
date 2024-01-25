package com.example.shop.public_interface;

import java.util.List;

public record ProductPageDto (
        List<ProductCommonDto> products,
        PaginationDto paginationDto
){
}

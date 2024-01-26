package com.example.shop.public_interface.product;

public record PaginationDto (
        int pageSize,
        int pageCount,
        int currentPage
){
}

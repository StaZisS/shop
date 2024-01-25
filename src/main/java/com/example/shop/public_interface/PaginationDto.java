package com.example.shop.public_interface;

public record PaginationDto (
        int pageSize,
        int pageCount,
        int currentPage
){
}

package com.example.shop.public_interface;

public record FilterDto(
        String productName,
        SortType sortType,
        PaginationProperty pagination
) {
    public record PaginationProperty(
        int pageSize,
        int pageNumber
    ){
    }

    public static FilterDto getDefault() {
        var pagination = new PaginationProperty(1, 1);
        return new FilterDto("", SortType.DEFAULT, pagination);
    }
}

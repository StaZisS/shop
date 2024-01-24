package com.example.shop.publicInterface;

public record FilterDto(
        String productName,
        SortType sortType
) {
    public static FilterDto getDefault() {
        return new FilterDto("", SortType.DEFAULT);
    }
}

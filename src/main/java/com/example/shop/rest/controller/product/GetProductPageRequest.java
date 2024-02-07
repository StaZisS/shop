package com.example.shop.rest.controller.product;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetProductPageRequest(
        @JsonProperty("product_name")
        String productName,
        @JsonProperty("sort_type")
        String sortType,
        @JsonProperty("page_size")
        int pageSize,
        @JsonProperty("page_number")
        int pageNumber
) {
}

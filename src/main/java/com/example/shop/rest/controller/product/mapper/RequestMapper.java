package com.example.shop.rest.controller.product.mapper;

import com.example.shop.public_interface.client.ClientCreateDto;
import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.SortType;
import com.example.shop.rest.controller.product.GetProductPageRequest;
import com.example.shop.rest.controller.auth.RegisterDto;

public class RequestMapper {
    public static FilterDto mapRequestToDto(GetProductPageRequest request) {
        var pagination = new FilterDto.PaginationProperty(
                request.pageSize(),
                request.pageNumber()
        );
        return new FilterDto(
                request.productName(),
                SortType.getSortTypeByName(request.sortType()),
                pagination
        );
    }

    public static ClientCreateDto mapRequestToDto(RegisterDto dto) {
        return new ClientCreateDto(
                dto.name(),
                dto.email(),
                dto.password(),
                dto.birthDate(),
                dto.gender()
        );
    }
}

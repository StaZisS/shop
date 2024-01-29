package com.example.shop.rest.controller.mapper;

import com.example.shop.public_interface.client.CreateClientDto;
import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.SortType;
import com.example.shop.rest.controller.GetProductPageRequest;
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

    public static CreateClientDto mapRequestToDto(RegisterDto dto) {
        return new CreateClientDto(
                dto.name(),
                dto.email(),
                dto.password(),
                dto.birthDate(),
                dto.gender()
        );
    }
}

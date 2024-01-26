package com.example.shop.rest.controller;

import com.example.shop.core.product.service.ProductService;
import com.example.shop.public_interface.product.ProductPageDto;
import com.example.shop.rest.controller.mapper.RequestMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@AllArgsConstructor
public class RestProductController {
    private final ProductService productService;

    @GetMapping
    public ProductPageDto getProductPage(@RequestBody GetProductPageRequest request) {
        var filterDto = RequestMapper.mapRequestToDto(request);
        return productService.getProducts(filterDto);
    }
}

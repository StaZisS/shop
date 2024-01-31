package com.example.shop.core.product.service;

import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.ProductPageDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductPageDto getProducts(@NonNull FilterDto filterDto) {
        validatePagination(filterDto);

        return productRepository.getCommonProductsPage(filterDto);
    }

    private void validatePagination(FilterDto filterDto) {
        var pagination = filterDto.pagination();
        if (pagination.pageNumber() < 0) {
            throw new ExceptionInApplication("Выход за пределы доступных страниц", ExceptionType.INVALID);
        }

        if (pagination.pageSize() <= 0) {
            throw new ExceptionInApplication("Количество элементов должно быть больше нуля", ExceptionType.INVALID);
        }

        var countProduct = productRepository.getCountProducts(filterDto.productName());
        var countPage = Math.ceil((double) countProduct / pagination.pageSize());

        if (pagination.pageNumber() > countPage) {
            throw new ExceptionInApplication("Выход за пределы доступных страниц", ExceptionType.INVALID);
        }
    }
}

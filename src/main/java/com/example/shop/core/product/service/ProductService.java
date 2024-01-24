package com.example.shop.core.product.service;

import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.publicInterface.FilterDto;
import com.example.shop.publicInterface.ProductCommonDto;
import com.example.shop.publicInterface.mapper.CommonProductMapEntityToDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductCommonDto> getProducts(@NonNull FilterDto filterDto) {
        var products = productRepository.getCommonProducts(filterDto);

        var sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(filterDto.sortType().getComparator());

        return sortedProducts.stream()
                .map(CommonProductMapEntityToDto::map)
                .collect(Collectors.toList());
    }
}

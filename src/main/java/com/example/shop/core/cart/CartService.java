package com.example.shop.core.cart;

import com.example.shop.public_interface.cart.CartDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void setCountProduct(CartDto dto) {
        validateSetCountDto(dto);

        if (dto.count() == 0) {
            cartRepository.deleteProductPosition(dto.clientId(), dto.productCode());
            return;
        }

        var cartEntity = new CartEntity(
                dto.clientId(),
                dto.productCode(),
                dto.count()
        );

        cartRepository.setCountProduct(cartEntity);
    }

    public void addProduct(CartDto dto) {
        var currentCount = cartRepository.getCountProduct(dto.clientId(), dto.productCode());

        var updatedDto = new CartDto(
                dto.clientId(),
                dto.productCode(),
                currentCount + 1
        );

        setCountProduct(updatedDto);
    }

    public void deleteProduct(CartDto dto) {
        var currentCount = cartRepository.getCountProduct(dto.clientId(), dto.productCode());

        var updatedDto = new CartDto(
                dto.clientId(),
                dto.productCode(),
                currentCount - 1
        );

        setCountProduct(updatedDto);
    }

    private void validateSetCountDto(CartDto dto) {
        if (dto.count() < 0) {
            throw new ExceptionInApplication("Невозможно установить количество товаров отрицательным", ExceptionType.INVALID);
        }
    }
}

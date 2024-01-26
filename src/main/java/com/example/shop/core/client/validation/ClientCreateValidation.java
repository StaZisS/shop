package com.example.shop.core.client.validation;

import com.example.shop.public_interface.client.CreateClientDto;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;

import java.util.List;
import java.util.regex.Pattern;

public class ClientCreateValidation {
    private static final List<String> AVAILABLE_GENDER = List.of("MALE", "FEMALE", "UNSPECIFIED");

    private static final String EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public static void validate(CreateClientDto dto) {
        checkGender(dto.gender());
        checkEmail(dto.email());
    }

    private static void checkGender(String gender) {
        if (AVAILABLE_GENDER.contains(gender)) return;
        throw new ExceptionInApplication("Указан неподдерживаемый гендер", ExceptionType.INVALID);
    }

    private static void checkEmail(String email) {
        var isCorrect = Pattern.compile(EMAIL_PATTERN)
                .matcher(email)
                .matches();

        if (isCorrect) return;
        throw new ExceptionInApplication("Указана неккоректная почта",ExceptionType.INVALID);
    }

}

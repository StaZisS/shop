package com.example.shop.grpc.auth;

import auth.JwtResponse;
import auth.LoginRequest;
import auth.RefreshJwtRequest;
import auth.RegisterRequest;
import com.example.shop.grpc.CasterType;
import com.example.shop.public_interface.auth.JwtResponseDto;
import com.example.shop.public_interface.auth.LoginDto;
import com.example.shop.public_interface.client.ClientCreateDto;

public class Mapper {
    public static ClientCreateDto mapRequestToDto(RegisterRequest request) {
        return new ClientCreateDto(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                CasterType.cast(request.getBirthDate()),
                request.getGender()
        );
    }

    public static JwtResponse mapDtoToResponse(JwtResponseDto dto) {
        return JwtResponse.newBuilder()
                .setAccessToken(dto.accessToken())
                .setRefreshToken(dto.refreshToken())
                .build();
    }

    public static LoginDto mapRequestToDto(LoginRequest request) {
        return new LoginDto(
                request.getEmail(),
                request.getPassword()
        );
    }

    public static String mapRequestToDto(RefreshJwtRequest request) {
        return request.getRefreshToken();
    }
}

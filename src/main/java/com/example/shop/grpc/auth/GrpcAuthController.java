package com.example.shop.grpc.auth;

import auth.AuthServiceGrpc.AuthServiceImplBase;
import auth.JwtResponse;
import auth.LoginRequest;
import auth.RefreshJwtRequest;
import auth.RegisterRequest;
import com.example.shop.core.auth.service.AuthService;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcAuthController extends AuthServiceImplBase {
    private final AuthService authService;

    @Override
    public void register(RegisterRequest request, StreamObserver<JwtResponse> responseObserver) {
        var clientCreateDto = Mapper.mapRequestToDto(request);
        var tokens = authService.register(clientCreateDto);

        var response = Mapper.mapDtoToResponse(tokens);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<JwtResponse> responseObserver) {
        var loginDto = Mapper.mapRequestToDto(request);
        var tokens = authService.login(loginDto);

        var response = Mapper.mapDtoToResponse(tokens);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAccessToken(RefreshJwtRequest request, StreamObserver<JwtResponse> responseObserver) {
        var refreshToken = Mapper.mapRequestToDto(request);
        var token = authService.getAccessToken(refreshToken);

        var response = Mapper.mapDtoToResponse(token);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getRefreshToken(RefreshJwtRequest request, StreamObserver<JwtResponse> responseObserver) {
        var refreshToken = Mapper.mapRequestToDto(request);
        var tokens = authService.refresh(refreshToken);

        var response = Mapper.mapDtoToResponse(tokens);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void logout(RefreshJwtRequest request, StreamObserver<Empty> responseObserver) {
        var refreshToken = Mapper.mapRequestToDto(request);
        authService.logout(refreshToken);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}

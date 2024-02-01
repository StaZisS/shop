package com.example.shop.grpc.configuration;

import com.example.shop.core.auth.provider.JwtProvider;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HeaderServerInterceptor implements ServerInterceptor {
    private static final String[] WHITE_LIST = new String[] {
            "login",
            "register",
            "getAccessToken"
    };

    private static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final JwtProvider jwtProvider;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> next
    ) {
        var methodCallName = serverCall.getMethodDescriptor().getBareMethodName();
        if (isMethodInWhiteList(methodCallName)) {
            return next.startCall(serverCall, metadata);
        }

        var accessToken = metadata.get(CUSTOM_HEADER_KEY);
        if (accessToken == null) {
            throw new ExceptionInApplication("Отсутствует AccessToken", ExceptionType.UNAUTHORIZED);
        }

        jwtProvider.validateAccessToken(accessToken);

        return next.startCall(serverCall, metadata);
    }

    private boolean isMethodInWhiteList(String methodCallName) {
        return Arrays.asList(WHITE_LIST).contains(methodCallName);
    }
}

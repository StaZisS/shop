package com.example.shop.grpc.configuration;

import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.util.Map;

@GrpcAdvice
public class ExceptionHandler {
    private static final Map<ExceptionType, Status> STATUS_MAP = Map.of(
            ExceptionType.FATAL, Status.INTERNAL,
            ExceptionType.INVALID, Status.INVALID_ARGUMENT,
            ExceptionType.ALREADY_EXISTS, Status.ALREADY_EXISTS,
            ExceptionType.NOT_FOUND, Status.NOT_FOUND,
            ExceptionType.UNAUTHORIZED, Status.UNAUTHENTICATED,
            ExceptionType.ILLEGAL, Status.INTERNAL
    );

    @GrpcExceptionHandler(ExceptionInApplication.class)
    public Status handleExpectedException(ExceptionInApplication exception) {
        var statusInMap = STATUS_MAP.get(exception.getType());
        return statusInMap.withDescription(exception.getMessage());
    }

    @GrpcExceptionHandler
    public Status handleException(Exception exception) {
        return Status.INTERNAL.withDescription("Internal server error").withCause(exception);
    }
}

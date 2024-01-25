package com.example.shop.grpc;

import com.example.shop.core.product.service.ProductService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import product.GetProductPageRequest;
import product.GetProductPageResponse;
import product.GetProductPageServiceGrpc;

@GrpcService
@RequiredArgsConstructor
public class ProductController extends GetProductPageServiceGrpc.GetProductPageServiceImplBase {
    private final ProductService productService;

    @Override
    public void getProductPage(GetProductPageRequest request, StreamObserver<GetProductPageResponse> responseObserver) {
        var filterDto = Mapper.mapRequestToDto(request);
        var productPage = productService.getProducts(filterDto);

        var response = Mapper.mapDtoToResponse(productPage);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

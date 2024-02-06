package com.example.shop.core.review.service;

import com.example.shop.core.order.repository.DeliveryStatus;
import com.example.shop.core.order.repository.OrderRepository;
import com.example.shop.core.review.repository.ReviewEntity;
import com.example.shop.core.review.repository.ReviewRepository;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import com.example.shop.public_interface.review.ReviewCreateDto;
import com.example.shop.public_interface.review.ReviewDeleteDto;
import com.example.shop.public_interface.review.ReviewUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final static DeliveryStatus STATUS_CAN_REVIEWED = DeliveryStatus.DELIVERED;

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public UUID createReview(ReviewCreateDto dto) {
        var isNotFirstReview = reviewRepository.getReviewByClientAndProduct(dto.clientId(), dto.productCode()).isPresent();
        if(isNotFirstReview) {
            throw new ExceptionInApplication("Ваш отзыв на это товар уже существует, изменить прошлый", ExceptionType.INVALID);
        }

        var ordersWithThisProduct = orderRepository.getAllOrdersWithProduct(dto.clientId(), dto.productCode());
        var isNotClientCanLeaveReview = ordersWithThisProduct.stream()
                .noneMatch(product -> product.status() == STATUS_CAN_REVIEWED);
        if (isNotClientCanLeaveReview) {
            throw new ExceptionInApplication("У вас нет завершенных заказов с этим товаром, следовательно вы не можете его оценить", ExceptionType.INVALID);
        }

        var reviewEntity = getDefaultReviewEntity(dto);
        reviewRepository.createReview(reviewEntity);

        return reviewEntity.reviewId();
    }

    public void updateReview(ReviewUpdateDto dto) {
        var reviewEntity = reviewRepository.getReviewById(dto.reviewId())
                .orElseThrow(() -> new ExceptionInApplication("Данного отзова не существует", ExceptionType.NOT_FOUND));
        var isNotThisClientReview = !reviewEntity.clientId().equals(dto.clientId());
        if(isNotThisClientReview) {
            throw new ExceptionInApplication("Этот отзыв другого клиента", ExceptionType.INVALID);
        }

        var updatedEntity = getUpdatedReviewEntity(reviewEntity, dto);
        reviewRepository.updateReview(updatedEntity);
    }

    public void deleteReview(ReviewDeleteDto dto) {
        var reviewEntity = reviewRepository.getReviewById(dto.reviewId())
                .orElseThrow(() -> new ExceptionInApplication("Данного отзова не существует", ExceptionType.NOT_FOUND));
        var isNotThisClientReview = !reviewEntity.clientId().equals(dto.clientId());
        if(isNotThisClientReview) {
            throw new ExceptionInApplication("Этот отзыв другого клиента", ExceptionType.INVALID);
        }

        reviewRepository.deleteReview(dto.reviewId());
    }

    private ReviewEntity getDefaultReviewEntity(ReviewCreateDto dto) {
        return new ReviewEntity(
                UUID.randomUUID(),
                dto.clientId(),
                dto.productCode(),
                OffsetDateTime.now(),
                null,
                dto.rating(),
                dto.reviewBody()
        );
    }

    private ReviewEntity getUpdatedReviewEntity(ReviewEntity entity, ReviewUpdateDto dto) {
        return new ReviewEntity(
                entity.reviewId(),
                entity.clientId(),
                entity.productCode(),
                entity.createdTime(),
                OffsetDateTime.now(),
                dto.rating(),
                dto.reviewBody()
        );
    }
}

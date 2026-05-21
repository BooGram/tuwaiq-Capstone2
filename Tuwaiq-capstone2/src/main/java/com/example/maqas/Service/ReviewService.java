package com.example.maqas.Service;

import com.example.maqas.Api.ApiException;
import com.example.maqas.Api.ApiResponse;
import com.example.maqas.Model.Review;
import com.example.maqas.Repository.ClothingOrderRepository;
import com.example.maqas.Repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ClothingOrderRepository clothingOrderRepository;

    public ApiResponse addReview(Integer shopId, Review review) {
        boolean hasDeliveredOrder = clothingOrderRepository
                .existsByCustomerIdAndShopIdAndStatus(review.getCustomerId(), shopId, "DELIVERED");

        if (!hasDeliveredOrder) {
            throw new ApiException("You can only review a shop after receiving your order");
        }

        boolean alreadyReviewed = reviewRepository
                .existsByCustomerIdAndShopId(review.getCustomerId(), shopId);

        if (alreadyReviewed) {
            throw new ApiException("You have already reviewed this shop");
        }

        review.setShopId(shopId);
        reviewRepository.save(review);
        return new ApiResponse("Review submitted successfully");
    }

    public List<Review> getShopReviews(Integer shopId) {
        return reviewRepository.findByShopId(shopId);
    }
}
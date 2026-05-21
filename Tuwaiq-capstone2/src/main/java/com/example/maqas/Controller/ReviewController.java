package com.example.maqas.Controller;

import com.example.maqas.Model.Review;
import com.example.maqas.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{shopId}")
    public ResponseEntity getShopReviews(@PathVariable Integer shopId) {
        return ResponseEntity.ok(reviewService.getShopReviews(shopId));
    }

    @PostMapping("/{shopId}")
    public ResponseEntity addReview(@PathVariable Integer shopId, @RequestBody @Valid Review review) {
        return ResponseEntity.ok(reviewService.addReview(shopId, review));
    }
}

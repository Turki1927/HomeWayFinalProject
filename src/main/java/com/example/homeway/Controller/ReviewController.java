package com.example.homeway.Controller;

import com.example.homeway.API.ApiResponse;
import com.example.homeway.DTO.In.ReviewDTOIn;
import com.example.homeway.Model.User;
import com.example.homeway.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //customer
    @PostMapping("/create")
    public ResponseEntity<?> createReview(@AuthenticationPrincipal User user, @Valid @RequestBody ReviewDTOIn dto) {
        reviewService.createReview(user, dto);
        return ResponseEntity.ok(new ApiResponse("review created"));
    }

    //company
    @GetMapping("/company/request/{requestId}")
    public ResponseEntity<?> getCompanyReviewByRequest(@AuthenticationPrincipal User user,@PathVariable Integer requestId) {
        return ResponseEntity.ok(reviewService.getReviewByRequestForCompany(user, requestId));
    }

    //company
    @GetMapping("/company/my")
    public ResponseEntity<?> getCompanyReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.getCompanyReviews(user));
    }

    //customer
    @GetMapping("/customer/request/{requestId}")
    public ResponseEntity<?> getCustomerReviewByRequest(@AuthenticationPrincipal User user,@PathVariable Integer requestId) {
        return ResponseEntity.ok(reviewService.getReviewByRequestForCustomer(user, requestId));
    }

    //customer
    @GetMapping("/customer/my")
    public ResponseEntity<?> getCustomerReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.getCustomerReviews(user));
    }
}

package com.example.homeway.Service;


import com.example.homeway.API.ApiException;
import com.example.homeway.DTO.In.ReviewDTOIn;
import com.example.homeway.DTO.Out.ReviewDTOOut;
import com.example.homeway.Model.*;
import com.example.homeway.Repository.NotificationRepository;
import com.example.homeway.Repository.RequestRepository;
import com.example.homeway.Repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RequestRepository requestRepository;
    private final NotificationRepository notificationRepository;

    //customer
    @Transactional
    public void createReview(User user, ReviewDTOIn dto) {

        if (user == null) throw new ApiException("unauthorized");
        Customer customer = user.getCustomer();
        if (customer == null) throw new ApiException("customer profile not found");

        Request request = requestRepository.findRequestById(dto.getRequestId());
        if (request == null) throw new ApiException("request not found with id: " + dto.getRequestId());

        if (request.getCustomer() == null || !request.getCustomer().getId().equals(customer.getId())) {
            throw new ApiException("you can only review your own request");
        }

        if (!"completed".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("you can only review a completed request");
        }

        //one review per request
        if (reviewRepository.existsByRequest_Id(request.getId())) {
            throw new ApiException("review already exists for this request");
        }

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setCustomer(customer);
        review.setRequest(request);

        reviewRepository.save(review);

        //notification to company
        if (request.getCompany() != null) {
            Notification n = new Notification();
            n.setTitle("New review received");
            n.setMessage("A customer posted a review for request id: " + request.getId());
            n.setCreated_at(LocalDateTime.now());
            n.setCompany(request.getCompany());
            notificationRepository.save(n);
        }
    }

    //company
    public ReviewDTOOut getReviewByRequestForCompany(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (request.getCompany() == null || !request.getCompany().getId().equals(company.getId())) {
            throw new ApiException("request is not assigned to this company");
        }

        Review review = reviewRepository.findReviewByRequest_Id(requestId);
        if (review == null) throw new ApiException("no review found for this request");

        return convertToDTO(review);
    }

    //company
    public List<ReviewDTOOut> getCompanyReviews(User user) {

        if (user == null) throw new ApiException("unauthorized");
        Company company = user.getCompany();
        if (company == null) throw new ApiException("company profile not found");

        List<ReviewDTOOut> outs = new ArrayList<>();
        for (Review r : reviewRepository.findAllByRequest_Company_Id(company.getId())) {
            outs.add(convertToDTO(r));
        }
        return outs;
    }

    //customer
    public ReviewDTOOut getReviewByRequestForCustomer(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");
        Customer customer = user.getCustomer();
        if (customer == null) throw new ApiException("customer profile not found");

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("request not found with id: " + requestId);

        if (request.getCustomer() == null || !request.getCustomer().getId().equals(customer.getId())) {
            throw new ApiException("you can only view reviews for your own requests");
        }

        Review review = reviewRepository.findReviewByRequest_Id(requestId);
        if (review == null) throw new ApiException("no review found for this request");

        return convertToDTO(review);
    }

    //customer get My reviews
    public List<ReviewDTOOut> getCustomerReviews(User user) {

        if (user == null) throw new ApiException("unauthorized");
        Customer customer = user.getCustomer();
        if (customer == null) throw new ApiException("customer profile not found");

        List<ReviewDTOOut> outs = new ArrayList<>();
        for (Review r : reviewRepository.findAllByCustomer_Id(customer.getId())) {
            outs.add(convertToDTO(r));
        }
        return outs;
    }

    public ReviewDTOOut convertToDTO(Review r) {
        return new ReviewDTOOut(
                r.getId(), r.getRating(), r.getComment(), r.getCreatedAt(),
                (r.getRequest() != null ? r.getRequest().getId() : null),
                (r.getCustomer() != null ? r.getCustomer().getId() : null),
                (r.getRequest() != null && r.getRequest().getCompany() != null ? r.getRequest().getCompany().getId() : null));
    }
}

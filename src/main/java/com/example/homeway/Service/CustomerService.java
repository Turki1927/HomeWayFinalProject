package com.example.homeway.Service;

import com.example.homeway.AIService.AIService;
import com.example.homeway.API.ApiException;
import com.example.homeway.DTO.Ai.DescriptionDTOIn;
import com.example.homeway.DTO.Ai.RepairChecklistDTOIn;
import com.example.homeway.DTO.Ai.ReviewAssistDTOIn;
import com.example.homeway.DTO.In.CustomerDTOIn;
import com.example.homeway.Model.*;
import com.example.homeway.Repository.OfferRepository;
import com.example.homeway.Repository.ReportRepository;
import com.example.homeway.Repository.RequestRepository;
import com.example.homeway.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private  final OfferRepository offerRepository;
    private final RequestRepository requestRepository;
    private final ReportRepository reportRepository;
    private final AIService aiService;


    public Customer getMyCustomer(User user) {
        if (user == null) {
            throw new ApiException("Unauthenticated");
        }

        if (user.getCustomer() == null) {
            throw new ApiException("Customer not found");
        }

        return user.getCustomer();
    }

    public void updateMyCustomer(User user, CustomerDTOIn dto) {
        if (user == null) {
            throw new ApiException("Unauthenticated");
        }

        if (user.getCustomer() == null) {
            throw new ApiException("Customer not found");
        }

        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setCountry(dto.getCountry());
        user.setCity(dto.getCity());
        user.setPassword(dto.getPassword());

        userRepository.save(user);
    }


    public void deleteMyCustomer(User user) {
        if (user == null) {
            throw new ApiException("Unauthenticated");
        }

        if (user.getCustomer() == null) {
            throw new ApiException("Customer not found");
        }

        userRepository.delete(user);
    }

    //Extra endpoints
    public void acceptOffer(User user, Integer offerId) {
        Customer customer = getMyCustomer(user);

        Offer offer = offerRepository.findOfferById(offerId);
        if (offer == null) throw new ApiException("Offer not found");

        Request request = offer.getRequest();
        if (request == null) throw new ApiException("Offer is not linked to a request");

        if (request.getCustomer() == null || !request.getCustomer().getId().equals(customer.getId())) {
            throw new ApiException("You are not allowed to access this offer");
        }

        if (!"PENDING".equalsIgnoreCase(offer.getStatus())) {
            throw new ApiException("Offer is not pending");
        }

        offer.setStatus("ACCEPTED");
        offerRepository.save(offer);
    }

    public void rejectOffer(User user, Integer offerId) {
        Customer customer = getMyCustomer(user);

        Offer offer = offerRepository.findOfferById(offerId);
        if (offer == null) throw new ApiException("Offer not found");

        Request request = offer.getRequest();
        if (request == null) throw new ApiException("Offer is not linked to a request");

        if (request.getCustomer() == null || !request.getCustomer().getId().equals(customer.getId())) {
            throw new ApiException("You are not allowed to access this offer");
        }

        if (!"PENDING".equalsIgnoreCase(offer.getStatus())) {
            throw new ApiException("Offer is not pending");
        }
        
        if (Boolean.TRUE.equals(request.getIsPaid())) {
            throw new ApiException("Cannot reject an offer after payment");
        }

        offer.setStatus("REJECTED");
        offerRepository.save(offer);
    }

    public String getTimelineEstimator(User user, Integer requestId) {

        if (user == null){
            throw new ApiException("unauthorized");
        }

        Customer customer = user.getCustomer();
        if (customer == null){
            throw new ApiException("customer profile not found");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (request.getCustomer() == null || !request.getCustomer().getId().equals(customer.getId())) {
            throw new ApiException("request does not belong to you");
        }

        String description = request.getDescription();
        if (description == null || description.isBlank()) {
            throw new ApiException("request description is empty");
        }

        return aiService.customerServicesTimeEstimation(description);
    }

    public String customerReviewWritingAssist(User user, Integer requestId, ReviewAssistDTOIn dto) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (request.getCustomer() == null || !request.getCustomer().getId().equals(user.getId())) {
            throw new ApiException("request does not belong to you");
        }

//        if (!request.getStatus().equalsIgnoreCase("completed")) {
//            throw new ApiException("you can only write a review after request completion");
//        }

        return aiService.customerReviewWritingAssist(dto.getNotes(), dto.getTone());
    }

    public String customerRequestCostEstimation(User user, DescriptionDTOIn dto) {

        if (user == null) throw new ApiException("unauthorized");

        Customer customer = user.getCustomer();
        if (customer == null) throw new ApiException("customer profile not found");

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        String description = dto.getDescription();
        if (description == null || description.isBlank()) {
            throw new ApiException("description is empty");
        }

        return aiService.customerRequestCostEstimation(description);
    }

    public String customerReportSummary(User user, Integer reportId) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        Customer customer = user.getCustomer();
        if (customer == null) {
            throw new ApiException("customer profile not found");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Report report = reportRepository.findReportById(reportId);
        if (report == null) {
            throw new ApiException("report not found with id: " + reportId);
        }

        Request request = report.getRequest();
        if (request == null) {
            throw new ApiException("report is not linked to a request");
        }

        if (request.getCustomer() == null ||
                !request.getCustomer().getId().equals(customer.getId())) {
            throw new ApiException("report does not belong to your request");
        }

        String description = report.getFindings();
        if (description == null || description.isBlank()) {
            throw new ApiException("report description is empty");
        }

        return aiService.customerReportSummary(description);
    }

    public String redesignScopeGenerator(User user, Integer requestId) {

        if (user == null) throw new ApiException("unauthorized");

        Customer customer = user.getCustomer();
        if (customer == null){
            throw new ApiException("customer profile not found");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        Request request = requestRepository.findRequestById(requestId);
        if (request == null) {
            throw new ApiException("request not found with id: " + requestId);
        }

        if (request.getCustomer() == null || !request.getCustomer().getId().equals(customer.getId())) {
            throw new ApiException("request does not belong to you");
        }

        if (request.getType() == null || !request.getType().equalsIgnoreCase("REDESIGN")) {
            throw new ApiException("this request is not a redesign request");
        }

        String description = request.getDescription();
        if (description == null || description.isBlank()) {
            throw new ApiException("request description is empty");
        }

        return aiService.redesignCompanyRedesignScope(description);
    }

    public String customerAskAIWhatServiceDoesTheIssueFits(User user, DescriptionDTOIn dto) {

        if (user == null) throw new ApiException("unauthorized");
        if (user.getCustomer() == null) throw new ApiException("customer profile not found");

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        return aiService.customerAskAIWhatServiceDoesTheIssueFits(dto.getDescription());
    }

    public String customerIsFixOrDesignCheaper(User user, DescriptionDTOIn dto) {

        if (user == null) throw new ApiException("unauthorized");
        if (user.getCustomer() == null) throw new ApiException("customer profile not found");

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        return aiService.customerIsFixOrDesignCheaper(dto.getDescription());
    }
    public String customerRedesignFromImage(User user, String imageUrl, String language) {

        if (user == null) {
            throw new ApiException("unauthorized");
        }

        if (user.getCustomer() == null) {
            throw new ApiException("customer profile not found");
        }

        if (!Boolean.TRUE.equals(user.getIsSubscribed())) {
            throw new ApiException("You must be subscribed to use AI features");
        }

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ApiException("image url is required");
        }

        return aiService.customerRedesignFromImage(imageUrl, language);
    }
}





package com.example.homeway.Service;

import com.example.homeway.API.ApiException;
import com.example.homeway.Model.SubscriptionPayment;
import com.example.homeway.Model.User;
import com.example.homeway.Model.UserSubscription;
import com.example.homeway.Repository.SubscriptionPaymentRepository;
import com.example.homeway.Repository.UserRepository;
import com.example.homeway.Repository.UserSubscriptionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SubscriptionPaymentService {

    private final UserSubscriptionRepository UserSubscriptionRepository;
    private final SubscriptionPaymentRepository subscriptionPaymentRepository;
    private final UserRepository userRepository;

    @Value("${moyasar.api.key}")

    private String apiKey;

    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    // ================== CREATE PAYMENT ==================
    public ResponseEntity<Map<String, String>> processPayment(
            SubscriptionPayment paymentRequest,
            Integer subscriptionId) {

        UserSubscription subscription =
                UserSubscriptionRepository.findUserSubscriptionById(subscriptionId);

        if (subscription == null) {
            throw new ApiException("Subscription not found");
        }

        paymentRequest.setName("Homeway User");

        paymentRequest.setAmount(subscription.getMonthlyPrice());
        paymentRequest.setCurrency("SAR");

        if (paymentRequest.getAmount() < subscription.getMonthlyPrice()) {
            throw new ApiException("Insufficient amount");
        }

        String callbackUrl = "https://dashboard.moyasar.com/payments";

        String requestBody = String.format(
                "source[type]=card" +
                        "&source[name]=%s" +
                        "&source[number]=%s" +
                        "&source[cvc]=%s" +
                        "&source[month]=%s" +
                        "&source[year]=%s" +
                        "&amount=%d" +
                        "&currency=%s" +
                        "&callback_url=%s",
                paymentRequest.getName(),
                paymentRequest.getNumber(),
                paymentRequest.getCvc(),
                paymentRequest.getMonth(),
                paymentRequest.getYear(),
                (int) (paymentRequest.getAmount() * 100), // هللة
                paymentRequest.getCurrency(),
                callbackUrl
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    MOYASAR_API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            throw new ApiException("Payment failed: " + e.getResponseBodyAsString());
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            String transactionId = json.get("id").asText();
            String transactionUrl =
                    json.get("source").get("transaction_url").asText();

            paymentRequest.setTransactionId(transactionId);
            paymentRequest.setRedirectToCompletePayment(transactionUrl);
            paymentRequest.setUserSubscription(subscription);
            paymentRequest.setPaymentDate(LocalDateTime.now());
            paymentRequest.setStatus(json.get("status").asText());

            subscriptionPaymentRepository.save(paymentRequest);

            Map<String, String> result = new HashMap<>();
            result.put("transactionId", transactionId);
            result.put("transactionUrl", transactionUrl);

            return ResponseEntity.ok(result);

        } catch (JsonProcessingException e) {
            throw new ApiException("Error parsing payment response");
        }
    }


    // ================== CHECK PAYMENT STATUS ==================
    public String subscribePaymentStatus(Integer userId, Integer subscriptionId) {

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        UserSubscription subscription =
                UserSubscriptionRepository.findUserSubscriptionById(subscriptionId);

        if (subscription == null) {
            throw new ApiException("Subscription not found");
        }

        if (!"PENDING".equalsIgnoreCase(subscription.getStatus())) {
            throw new ApiException("Subscription already confirmed or invalid");
        }

        SubscriptionPayment payment =
                subscriptionPaymentRepository.findPaymentByUserSubscription(subscription);

        if (payment == null) {
            throw new ApiException("Payment not found");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_API_URL + payment.getTransactionId(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            String paymentStatus = json.get("status").asText();

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                subscription.setStatus("ACTIVE");
                //  subscription.setNextBillingDate(LocalDateTime.now().plusDays(2));
                subscription.setNextBillingDate(LocalDateTime.now().plusMonths(1));
                subscription.getUser().setIsSubscribed(true);

                UserSubscriptionRepository.save(subscription);
                userRepository.save(subscription.getUser());
            }

            return response.getBody();

        } catch (Exception e) {
            throw new ApiException("Failed to parse payment status");
        }
    }

    // ================== CONFIRM PAYMENT ==================
    public void updateAndConfirmPayment(
            Integer subscriptionId,
            String transactionId,
            Integer userId
    ) throws JsonProcessingException {

        UserSubscription subscription =
                UserSubscriptionRepository.findUserSubscriptionById(subscriptionId);

        if (subscription == null) {
            throw new ApiException("Subscription not found");
        }

        if ("ACTIVE".equalsIgnoreCase(subscription.getStatus())) {
            throw new ApiException("Subscription already active");
        }

        SubscriptionPayment payment =
                subscriptionPaymentRepository.findPaymentByUserSubscription(subscription);

        if (payment == null) {
            throw new ApiException("Payment not found");
        }

        User user = userRepository.findUserById(userId);
        if (user == null || !subscription.getUser().equals(user)) {
            throw new ApiException("Not authorized");
        }

        String txId = (transactionId != null && !transactionId.isEmpty()) ? transactionId : payment.getTransactionId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_API_URL + txId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        if (!json.has("status")) {
            throw new ApiException("Payment confirmation failed");
        }

        String status = json.get("status").asText();
        payment.setStatus(status);

        if ("paid".equalsIgnoreCase(status)) {

            payment.setPaymentDate(LocalDateTime.now());

            subscription.setStatus("ACTIVE");
            subscription.setNextBillingDate(LocalDateTime.now().plusMonths(1));

            subscription.getUser().setIsSubscribed(true);

            UserSubscriptionRepository.save(subscription);
            userRepository.save(subscription.getUser());

        } else {
            subscription.setStatus("FAILED");
            UserSubscriptionRepository.save(subscription);
        }

        subscriptionPaymentRepository.save(payment);
    }

}

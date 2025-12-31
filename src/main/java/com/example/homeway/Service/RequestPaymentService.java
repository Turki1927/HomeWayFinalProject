package com.example.homeway.Service;

import com.example.homeway.API.ApiException;
import com.example.homeway.Model.Offer;
import com.example.homeway.Model.Request;
import com.example.homeway.Model.User;
import com.example.homeway.Model.PaymentRequest;
import com.example.homeway.Repository.OfferRepository;
import com.example.homeway.Repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
public class RequestPaymentService {

   @Value("${app.base.url}")
    private String baseUrl;

    @Value("${moyasar.api.key}")
    private String apikey;

    private static final String MOYASAR_CREATE_URL = "https://api.moyasar.com/v1/payments";
    private static final String MOYASAR_GET_URL = "https://api.moyasar.com/v1/payments/";

    private final OfferRepository offerRepository;
    private final RequestRepository requestRepository;

    public ResponseEntity<String> processCardPayment(PaymentRequest paymentRequest,
                                                     double amount,
                                                     String currency,
                                                     String callbackUrl,
                                                     String description) {


        int amountHalala = (int) (amount * 100);

        String requestBody = String.format(
                "source[type]=card&source[name]=%s&source[number]=%s&source[cvc]=%s&" +
                        "source[month]=%s&source[year]=%s&amount=%d&currency=%s&description=%s&callback_url=%s",
                paymentRequest.getName(),
                paymentRequest.getNumber(),
                paymentRequest.getCvc(),
                paymentRequest.getMonth(),
                paymentRequest.getYear(),
                amountHalala,
                currency,
                description,
                callbackUrl
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apikey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_CREATE_URL, HttpMethod.POST, entity, String.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }


    // Offer payment endpoint
    public ResponseEntity<String> payOffer(User user, Integer offerId, PaymentRequest paymentRequest) {
        if (user == null) throw new ApiException("Unauthenticated");
        if (user.getCustomer() == null) throw new ApiException("Only customers can pay");

        Offer offer = offerRepository.findOfferById(offerId);
        if (offer == null) throw new ApiException("Offer not found");

        Request request = offer.getRequest();
        if (request == null) throw new ApiException("Offer is not linked to a request");

        if (request.getCustomer() == null || !request.getCustomer().getId().equals(user.getCustomer().getId())) {
            throw new ApiException("You are not allowed to pay for this offer");
        }

        if (!"ACCEPTED".equalsIgnoreCase(offer.getStatus())) {
            throw new ApiException("Offer must be accepted before payment");
        }

        if (Boolean.TRUE.equals(request.getIsPaid())) {
            throw new ApiException("Request already paid");
        }

        double amount = offer.getPrice();


        String currency = (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().isBlank())
                ? "SAR"
                : paymentRequest.getCurrency();

        String callbackUrl = baseUrl + "/api/v1/payment/callBack?offerId=" + offerId;


        String description = "HomeWay Offer #" + offerId;

        return processCardPayment(paymentRequest, amount, currency, callbackUrl, description);
    }

    // confirm payment then update request.isPaid automatically
    public void confirmOfferPayment(Integer offerId, String paymentId) {

        Offer offer = offerRepository.findOfferById(offerId);
        if (offer == null) throw new ApiException("Offer not found");

        Request request = offer.getRequest();
        if (request == null) throw new ApiException("Offer is not linked to a request");

        if (!"ACCEPTED".equalsIgnoreCase(offer.getStatus())) {
            throw new ApiException("Offer must be accepted before confirming payment");
        }

        if (Boolean.TRUE.equals(request.getIsPaid())) {
            throw new ApiException("Request already paid");
        }

        // fetch payment details from Moyasar
        String paymentJson = getPaymentStatus(paymentId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(paymentJson);

            String status = root.get("status").asText();
            int amount = root.get("amount").asInt();
            String currency = root.get("currency").asText();

            int expectedAmount = (int) (offer.getPrice() * 100);

            boolean isPaid = "paid".equalsIgnoreCase(status) || "captured".equalsIgnoreCase(status);
            if (!isPaid) {
                throw new ApiException("Payment is not paid yet");
            }

            if (amount != expectedAmount) {
                throw new ApiException("Payment amount does not match offer price");
            }

            if (!"SAR".equalsIgnoreCase(currency)) {
                throw new ApiException("Payment currency is not SAR");
            }

            request.setIsPaid(true);
            requestRepository.save(request);

            offer.setStatus("paid");
            offerRepository.save(offer);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Failed to verify payment response");
        }
    }


    // Callback helper just for testing
    public void markRequestPaid(Integer requestId) {
        Request request = requestRepository.findRequestById(requestId);
        if (request == null) throw new ApiException("Request not found");

        request.setIsPaid(true);
        requestRepository.save(request);
    }


    // Get payment status from Moyasar
    public String getPaymentStatus(String paymentId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apikey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_GET_URL + paymentId, HttpMethod.GET, entity, String.class
        );

        return response.getBody();
    }
}

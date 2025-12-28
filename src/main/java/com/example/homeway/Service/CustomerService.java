package com.example.homeway.Service;

import com.example.homeway.API.ApiException;
import com.example.homeway.DTO.In.CustomerDTOIn;
import com.example.homeway.Model.Customer;
import com.example.homeway.Model.Offer;
import com.example.homeway.Model.Request;
import com.example.homeway.Model.User;
import com.example.homeway.Repository.OfferRepository;
import com.example.homeway.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private  final OfferRepository offerRepository;

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
}





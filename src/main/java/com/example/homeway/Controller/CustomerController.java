package com.example.homeway.Controller;
import com.example.homeway.AIService.AIService;
import com.example.homeway.API.ApiResponse;
import com.example.homeway.DTO.Ai.DescriptionDTOIn;
import com.example.homeway.DTO.Ai.ReviewAssistDTOIn;
import com.example.homeway.DTO.In.CustomerDTOIn;
import com.example.homeway.Model.User;
import com.example.homeway.Service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/get")
    public ResponseEntity<?> getMyCustomer(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(customerService.getMyCustomer(user));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateMyCustomer(@AuthenticationPrincipal User user, @RequestBody @Valid CustomerDTOIn dto) {
        customerService.updateMyCustomer(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Customer updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMyCustomer(@AuthenticationPrincipal User user) {
        customerService.deleteMyCustomer(user);
        return ResponseEntity.status(200).body(new ApiResponse("Customer deleted successfully"));
    }

    @GetMapping("/ai/ServiceEstimator/{requestId}")
    public ResponseEntity<?> customerTimelineEstimator(@AuthenticationPrincipal User user, @PathVariable Integer requestId){
        String result = customerService.getTimelineEstimator(user, requestId);
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/review-assist/{requestId}")
    public ResponseEntity<?> reviewAssist(@AuthenticationPrincipal User user, @PathVariable Integer requestId,@RequestBody @Valid ReviewAssistDTOIn dto) {
        return ResponseEntity.status(200).body(customerService.customerReviewWritingAssist(user, requestId, dto));
    }

    @GetMapping("/customer/report-summary/{reportId}")
    public ResponseEntity<?> customerReportSummary(@AuthenticationPrincipal User user, @PathVariable Integer reportId) {
        return ResponseEntity.status(200).body(customerService.customerReportSummary(user, reportId));
    }

    @GetMapping("/redesign-scope/{requestId}")
    public ResponseEntity<?> redesignScope(@AuthenticationPrincipal User user, @PathVariable Integer requestId) {
        String result = customerService.redesignScopeGenerator(user, requestId);
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/cost-estimation")
    public ResponseEntity<?> customerRequestCostEstimation(@AuthenticationPrincipal User user, @RequestBody @Valid DescriptionDTOIn dto) {
        return ResponseEntity.status(200).body(customerService.customerRequestCostEstimation(user, dto));
    }

    @PostMapping("/service-fit")
    public ResponseEntity<?> customerAskAIWhatServiceDoesTheIssueFits(@AuthenticationPrincipal User user, @RequestBody @Valid DescriptionDTOIn dto) {
        return ResponseEntity.status(200).body(customerService.customerAskAIWhatServiceDoesTheIssueFits(user, dto));
    }

    @PostMapping("/fix-vs-redesign")
    public ResponseEntity<?> customerIsFixOrDesignCheaper(@AuthenticationPrincipal User user, @RequestBody @Valid DescriptionDTOIn dto) {
        return ResponseEntity.status(200).body(customerService.customerIsFixOrDesignCheaper(user, dto));
    }

    //Extra endpoints
    @PutMapping("/offer/accept/{offerId}")
    public ResponseEntity<?> acceptOffer(@AuthenticationPrincipal User user, @PathVariable Integer offerId) {
        customerService.acceptOffer(user, offerId);
        return ResponseEntity.status(200).body(new ApiResponse("Offer accepted, proceed to payment "));
    }

    @PutMapping("/offer/reject/{offerId}")
    public ResponseEntity<?> rejectOffer(@AuthenticationPrincipal User user, @PathVariable Integer offerId) {
        customerService.rejectOffer(user, offerId);
        return ResponseEntity.status(200).body(new ApiResponse("Offer rejected"));
    }

    @PostMapping("/ai/redesign-from-image/{language}")
    public ResponseEntity<?> redesignFromImage(@AuthenticationPrincipal User user, @RequestBody @Valid DescriptionDTOIn dto, @PathVariable String language) {
        String result = customerService.customerRedesignFromImage(user, dto.getDescription(), language);
        return ResponseEntity.status(200).body(result);
    }
}

package com.example.homeway.Controller;

import com.example.homeway.API.APIResponse;
import com.example.homeway.DTO.In.CompanyDTOIn;
import com.example.homeway.DTO.In.CustomerDTOIn;
import com.example.homeway.DTO.In.WorkerDTOIn;
import com.example.homeway.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerDTOIn dto) {
        authService.registerCustomer(dto);
        return ResponseEntity.status(200).body(new APIResponse("Customer registered successfully: " + dto.getUsername()));
    }

    @PostMapping("/register/company")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody CompanyDTOIn dto) {
        authService.registerCompany(dto);
        return ResponseEntity.status(200).body(new APIResponse("Company registered successfully: " + dto.getUsername()));
    }

    @PostMapping("/register/worker")
    public ResponseEntity<?> registerWorker(@Valid @RequestBody WorkerDTOIn dto) {
        authService.registerWorker(dto);
        return ResponseEntity.status(200).body(new APIResponse("Worker registered successfully: " + dto.getUsername()));
    }

}
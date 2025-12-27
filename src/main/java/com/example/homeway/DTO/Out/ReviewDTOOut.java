package com.example.homeway.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewDTOOut {
    private Integer id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    private Integer requestId;
    private Integer customerId;
    private Integer companyId;
}

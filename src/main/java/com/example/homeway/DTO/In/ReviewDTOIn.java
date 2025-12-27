package com.example.homeway.DTO.In;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTOIn {

    @NotNull
    private Integer requestId;

    @NotNull
    @Min(0)
    @Max(5)
    private Integer rating;

    @NotEmpty
    private String comment;
}

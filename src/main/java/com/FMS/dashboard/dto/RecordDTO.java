package com.FMS.dashboard.dto;

import com.FMS.dashboard.model.RecordType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RecordDTO {

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private RecordType type;

    @NotBlank
    private String category;

    @NotNull
    private LocalDate date;

    private String description;
}
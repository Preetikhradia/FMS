package com.FMS.dashboard.dto.record;

import com.FMS.dashboard.model.RecordType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * All fields are optional — pass only the ones you want to filter by.
 * Example: GET /api/records?type=INCOME&category=Salary&from=2024-01-01&page=0&size=20
 */
@Data
public class RecordFilterRequest {

    private RecordType type;            // INCOME | EXPENSE | null (both)

    private String category;            // exact match, case-insensitive

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;             // range start (inclusive)

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;               // range end   (inclusive)

    // Pagination — safe defaults so the client never has to pass these
    private int page = 0;
    private int size = 20;
}
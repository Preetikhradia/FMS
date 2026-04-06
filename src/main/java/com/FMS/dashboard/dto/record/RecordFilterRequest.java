package com.FMS.dashboard.dto.record;

import com.FMS.dashboard.model.RecordType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
public class RecordFilterRequest {

    private RecordType type;

    private String category;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate to;

    private int page = 0;
    private int size = 20;
}
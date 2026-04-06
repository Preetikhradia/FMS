package com.FMS.dashboard.dto.dashboard;

import com.FMS.dashboard.model.RecordType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivity {

    private Long id;
    private BigDecimal amount;
    private RecordType type;
    private String category;
    private LocalDate date;
}
package com.FMS.dashboard.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTrend {

    private int year;
    private int month;
    private BigDecimal income;
    private BigDecimal expenses;

    
    public BigDecimal getNet() {
        if (income == null && expenses == null) return BigDecimal.ZERO;
        BigDecimal inc = income   != null ? income   : BigDecimal.ZERO;
        BigDecimal exp = expenses != null ? expenses : BigDecimal.ZERO;
        return inc.subtract(exp);
    }
}
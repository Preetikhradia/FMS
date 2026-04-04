package com.FMS.dashboard.repository;

import com.FMS.dashboard.model.FinancialRecord;
import com.FMS.dashboard.model.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<FinancialRecord, Long> {

    // FILTER BY TYPE
    List<FinancialRecord> findByType(RecordType type);

    // FILTER BY CATEGORY
    List<FinancialRecord> findByCategory(String category);

    // FILTER BY DATE RANGE
    List<FinancialRecord> findByDateBetween(LocalDate start, LocalDate end);

    // FILTER BY USER
    List<FinancialRecord> findByUserId(Long userId);

    // TOTAL INCOME (SAFE ENUM USAGE)
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type")
    Double getTotalByType(RecordType type);

    Optional<Object> getCategoryTotals();
}
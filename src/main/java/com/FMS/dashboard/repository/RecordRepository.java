package com.FMS.dashboard.repository;

import com.FMS.dashboard.model.FinancialRecord;
import com.FMS.dashboard.model.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<FinancialRecord, Long> {

    @Query("SELECT r FROM FinancialRecord r WHERE r.id = :id AND r.deleted = false")
    Optional<FinancialRecord> findActiveById(@Param("id") Long id);

    Page<FinancialRecord> findByDeletedFalseOrderByDateDesc(Pageable pageable);

    @Query("""
        SELECT r FROM FinancialRecord r
        WHERE r.deleted = false
          AND (:type     IS NULL OR r.type     = :type)
          AND (:category IS NULL OR LOWER(r.category) = LOWER(:category))
          AND (:from     IS NULL OR r.date    >= :from)
          AND (:to       IS NULL OR r.date    <= :to)
        ORDER BY r.date DESC
        """)
    Page<FinancialRecord> findFiltered(
            @Param("type")     RecordType type,
            @Param("category") String     category,
            @Param("from")     LocalDate  from,
            @Param("to")       LocalDate  to,
            Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(r.amount), 0)
        FROM FinancialRecord r
        WHERE r.deleted = false AND r.type = :type
        """)
    BigDecimal sumByType(@Param("type") RecordType type);

    @Query("""
        SELECT r.category, COALESCE(SUM(r.amount), 0)
        FROM FinancialRecord r
        WHERE r.deleted = false
        GROUP BY r.category
        """)
    List<Object[]> getCategoryTotals();
    @Query("""
        SELECT YEAR(r.date),
               MONTH(r.date),
               COALESCE(SUM(CASE WHEN r.type = :income  THEN r.amount ELSE 0 END), 0),
               COALESCE(SUM(CASE WHEN r.type = :expense THEN r.amount ELSE 0 END), 0)
        FROM FinancialRecord r
        WHERE r.deleted = false
          AND r.date >= :from
        GROUP BY YEAR(r.date), MONTH(r.date)
        ORDER BY YEAR(r.date) ASC, MONTH(r.date) ASC
        """)
    List<Object[]> getMonthlyTrends(
            @Param("from")    LocalDate  from,
            @Param("income")  RecordType income,
            @Param("expense") RecordType expense);
}
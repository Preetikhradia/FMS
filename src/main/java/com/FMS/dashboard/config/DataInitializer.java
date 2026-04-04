package com.FMS.dashboard.config;

import com.FMS.dashboard.model.FinancialRecord;
import com.FMS.dashboard.model.RecordType;
import com.FMS.dashboard.model.Role;
import com.FMS.dashboard.model.User;
import com.FMS.dashboard.repository.RecordRepository;
import com.FMS.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository   userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final RecordRepository recordRepository;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedSampleUsers();
        seedSampleRecords();
    }

    // ── Admin ─────────────────────────────────────────────────────────────────
    private void seedAdmin() {
        if (userRepository.existsByEmail("admin@fms.com")) {
            log.info("Admin already exists — skipping");
            return;
        }

        userRepository.save(User.builder()
                .email("admin@fms.com")
                .password(passwordEncoder.encode("admin123"))
                .name("System Admin")
                .role(Role.ADMIN)
                .active(true)
                .build());

        log.info("====================================================");
        log.info("  Default admin created");
        log.info("  Email   : admin@fms.com");
        log.info("  Password: admin123");
        log.info("  Role    : ADMIN");
        log.info("====================================================");
    }

    // ── Sample users (one per role for permission testing) ────────────────────
    private void seedSampleUsers() {
        createUserIfAbsent("analyst@fms.com", "analyst123", "Demo Analyst", Role.ANALYST);
        createUserIfAbsent("viewer@fms.com",  "viewer123",  "Demo Viewer",  Role.VIEWER);
    }

    private void createUserIfAbsent(String email, String password,
                                    String name, Role role) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .name(name)
                    .role(role)
                    .active(true)
                    .build());
            log.info("Seeded user: {} ({})", email, role);
        }
    }

    // ── Sample financial records (so charts render on first boot) ─────────────
    private void seedSampleRecords() {
        if (recordRepository.count() > 0) {
            log.info("Records already exist — skipping seed");
            return;
        }

        User admin = userRepository.findByEmailAndActiveTrue("admin@fms.com")
                .orElse(null);
        if (admin == null) {
            log.warn("Admin not found — cannot seed records");
            return;
        }

        LocalDate now      = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        LocalDate twoMonthsAgo = now.minusMonths(2);

        List<FinancialRecord> records = List.of(

                // ── Income ────────────────────────────────────────────────────────
                record(admin, "85000", RecordType.INCOME,  "Salary",    now),
                record(admin, "85000", RecordType.INCOME,  "Salary",    lastMonth),
                record(admin, "85000", RecordType.INCOME,  "Salary",    twoMonthsAgo),
                record(admin, "12000", RecordType.INCOME,  "Freelance", now),
                record(admin, "8500",  RecordType.INCOME,  "Freelance", lastMonth),
                record(admin, "5000",  RecordType.INCOME,  "Bonus",     twoMonthsAgo),

                // ── Expenses ──────────────────────────────────────────────────────
                record(admin, "15000", RecordType.EXPENSE, "Rent",       now),
                record(admin, "15000", RecordType.EXPENSE, "Rent",       lastMonth),
                record(admin, "15000", RecordType.EXPENSE, "Rent",       twoMonthsAgo),
                record(admin, "3500",  RecordType.EXPENSE, "Utilities",  now),
                record(admin, "3200",  RecordType.EXPENSE, "Utilities",  lastMonth),
                record(admin, "2800",  RecordType.EXPENSE, "Utilities",  twoMonthsAgo),
                record(admin, "4200",  RecordType.EXPENSE, "Groceries",  now),
                record(admin, "3900",  RecordType.EXPENSE, "Groceries",  lastMonth),
                record(admin, "4100",  RecordType.EXPENSE, "Groceries",  twoMonthsAgo),
                record(admin, "1800",  RecordType.EXPENSE, "Transport",  now),
                record(admin, "2100",  RecordType.EXPENSE, "Transport",  lastMonth),
                record(admin, "900",   RecordType.EXPENSE, "Dining",     now),
                record(admin, "1200",  RecordType.EXPENSE, "Dining",     lastMonth)
        );

        recordRepository.saveAll(records);
        log.info("Seeded {} sample financial records", records.size());
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private FinancialRecord record(User createdBy, String amount,
                                   RecordType type, String category,
                                   LocalDate date) {
        return FinancialRecord.builder()
                .amount(new BigDecimal(amount))
                .type(type)
                .category(category)
                .date(date)
                .createdBy(createdBy)
                .deleted(false)
                .build();
    }
}
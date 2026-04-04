package com.FMS.dashboard.service;

import com.FMS.dashboard.dto.record.CreateRecordRequest;
import com.FMS.dashboard.dto.record.RecordFilterRequest;
import com.FMS.dashboard.dto.record.RecordResponse;
import com.FMS.dashboard.exception.AppException;
import com.FMS.dashboard.model.FinancialRecord;
import com.FMS.dashboard.model.User;
import com.FMS.dashboard.port.in.RecordUseCase;
import com.FMS.dashboard.repository.RecordRepository;
import com.FMS.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialRecordService implements RecordUseCase {

    private final RecordRepository recordRepository;
    private final UserRepository   userRepository;

    // VIEWER, ANALYST, ADMIN can all read
    @Override
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Transactional(readOnly = true)
    public Page<RecordResponse> getRecords(RecordFilterRequest filter) {
        return recordRepository.findFiltered(
                filter.getType(),
                filter.getCategory(),
                filter.getFrom(),
                filter.getTo(),
                PageRequest.of(filter.getPage(), filter.getSize())
        ).map(this::toResponse);
    }

    @Override
    @PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')")
    @Transactional(readOnly = true)
    public RecordResponse getById(Long id) {
        return toResponse(findActiveOrThrow(id));
    }

    // Only ADMIN can create, update, delete
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public RecordResponse create(CreateRecordRequest request) {
        User creator = currentUser();
        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(creator)
                .deleted(false)
                .build();
        FinancialRecord saved = recordRepository.save(record);
        log.info("Record created: id={} type={} amount={}", saved.getId(), saved.getType(), saved.getAmount());
        return toResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public RecordResponse update(Long id, CreateRecordRequest request) {
        FinancialRecord record = findActiveOrThrow(id);
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());
        return toResponse(recordRepository.save(record));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long id) {
        FinancialRecord record = findActiveOrThrow(id);
        record.setDeleted(true);        // soft delete — row stays in DB for audit
        recordRepository.save(record);
        log.info("Record soft-deleted: id={}", id);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private FinancialRecord findActiveOrThrow(Long id) {
        return recordRepository.findActiveById(id)
                .orElseThrow(() -> AppException.notFound("Record not found with id: " + id));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> AppException.unauthorized("Authenticated user not found"));
    }

    private RecordResponse toResponse(FinancialRecord r) {
        return RecordResponse.builder()
                .id(r.getId())
                .amount(r.getAmount())
                .type(r.getType())
                .category(r.getCategory())
                .date(r.getDate())
                .notes(r.getNotes())
                .createdBy(r.getCreatedBy().getEmail())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
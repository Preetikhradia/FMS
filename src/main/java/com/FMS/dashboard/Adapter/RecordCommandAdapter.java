package com.FMS.dashboard.Adapter;

import com.FMS.dashboard.dto.record.CreateRecordRequest;
import com.FMS.dashboard.dto.record.RecordFilterRequest;
import com.FMS.dashboard.dto.record.RecordResponse;
import com.FMS.dashboard.port.in.RecordUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter: bridges the HTTP controller to the RecordUseCase port.
 *
 * Responsibilities:
 *   - Translate / enrich incoming DTOs if needed
 *   - Log the inbound call for traceability
 *   - Delegate to the port — never contain business logic
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RecordCommandAdapter {

    private final RecordUseCase recordUseCase;

    public Page<RecordResponse> fetchRecords(RecordFilterRequest filter) {
        log.debug("Adapter → fetchRecords | filter={}", filter);
        return recordUseCase.getRecords(filter);
    }

    public RecordResponse fetchById(Long id) {
        log.debug("Adapter → fetchById | id={}", id);
        return recordUseCase.getById(id);
    }

    public RecordResponse createRecord(CreateRecordRequest request) {
        log.info("Adapter → createRecord | type={} category={}", request.getType(), request.getCategory());
        // Example enrichment: normalise category to title-case before passing down
        request.setCategory(toTitleCase(request.getCategory()));
        return recordUseCase.create(request);
    }

    public RecordResponse updateRecord(Long id, CreateRecordRequest request) {
        log.info("Adapter → updateRecord | id={}", id);
        request.setCategory(toTitleCase(request.getCategory()));
        return recordUseCase.update(id, request);
    }

    public void deleteRecord(Long id) {
        log.info("Adapter → deleteRecord | id={}", id);
        recordUseCase.delete(id);
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private String toTitleCase(String input) {
        if (input == null || input.isBlank()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
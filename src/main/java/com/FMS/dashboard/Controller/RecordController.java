package com.FMS.dashboard.Controller;

import com.FMS.dashboard.Adapter.RecordCommandAdapter;
import com.FMS.dashboard.dto.record.CreateRecordRequest;
import com.FMS.dashboard.dto.record.RecordFilterRequest;
import com.FMS.dashboard.dto.record.RecordResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    // ← ONLY the adapter is injected — service is invisible from here
    private final RecordCommandAdapter recordAdapter;

    @GetMapping
    public ResponseEntity<Page<RecordResponse>> list(@ModelAttribute RecordFilterRequest filter) {
        return ResponseEntity.ok(recordAdapter.fetchRecords(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recordAdapter.fetchById(id));
    }

    @PostMapping
    public ResponseEntity<RecordResponse> create(@Valid @RequestBody CreateRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recordAdapter.createRecord(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecordResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateRecordRequest request) {
        return ResponseEntity.ok(recordAdapter.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recordAdapter.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
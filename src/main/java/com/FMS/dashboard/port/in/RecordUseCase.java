package com.FMS.dashboard.port.in;

import com.FMS.dashboard.dto.record.CreateRecordRequest;
import com.FMS.dashboard.dto.record.RecordFilterRequest;
import com.FMS.dashboard.dto.record.RecordResponse;
import org.springframework.data.domain.Page;

public interface RecordUseCase {
    Page<RecordResponse> getRecords(RecordFilterRequest filter);
    RecordResponse getById(Long id);
    RecordResponse create(CreateRecordRequest request);
    RecordResponse update(Long id, CreateRecordRequest request);
    void delete(Long id);
}
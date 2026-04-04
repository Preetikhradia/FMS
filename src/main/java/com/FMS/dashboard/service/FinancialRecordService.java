package com.FMS.dashboard.service;

import com.FMS.dashboard.dto.RecordDTO;
import com.FMS.dashboard.model.FinancialRecord;
import com.FMS.dashboard.model.Role;
import com.FMS.dashboard.model.User;
import com.FMS.dashboard.port.in.RecordUseCase;
import com.FMS.dashboard.repository.RecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialRecordService implements RecordUseCase {

    private final RecordRepository recordRepository;

    public FinancialRecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public FinancialRecord createRecord(RecordDTO dto, User user) {

        if (user.getRole() == Role.VIEWER) {
            throw new RuntimeException("Access Denied");
        }

        FinancialRecord record = new FinancialRecord();
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setDate(dto.getDate());
        record.setDescription(dto.getDescription());
        record.setCreatedBy(user);

        return recordRepository.save(record);
    }

    public List<FinancialRecord> getAllRecords() {
        return recordRepository.findAll();
    }

    @Override
    public Page<RecordResponse> getRecords(RecordFilterRequest filter) {
        return null;
    }

    @Override
    public RecordResponse getById(Long id) {
        return null;
    }

    @Override
    public RecordResponse create(CreateRecordRequest request) {
        return null;
    }

    @Override
    public RecordResponse update(Long id, CreateRecordRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}

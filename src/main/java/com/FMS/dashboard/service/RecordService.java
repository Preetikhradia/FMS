package com.FMS.dashboard.service;

import com.FMS.dashboard.dto.RecordDTO;
import com.FMS.dashboard.model.FinancialRecord;
import com.FMS.dashboard.model.Role;
import com.FMS.dashboard.model.User;
import com.FMS.dashboard.model.*;
import com.FMS.dashboard.repository.RecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
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
}

package com.FMS.dashboard.Controller;


import com.FMS.dashboard.dto.RecordDTO;
import com.FMS.dashboard.model.*;
import com.FMS.dashboard.service.RecordService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    // MOCK USER (for now)
    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN); // change role to test
        return user;
    }

    @PostMapping
    public FinancialRecord createRecord(@Valid @RequestBody RecordDTO dto) {
        return recordService.createRecord(dto, getMockUser());
    }

    @GetMapping
    public List<FinancialRecord> getAll() {
        return recordService.getAllRecords();
    }
}
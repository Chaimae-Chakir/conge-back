package agilisys.conge.controller;

import agilisys.conge.dto.LeaveTypeDto;
import agilisys.conge.entity.LeaveType;
import agilisys.conge.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveTypeController {
    private final LeaveTypeService leaveTypeService;

    @GetMapping
    public ResponseEntity<List<LeaveTypeDto>> getAllActiveLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getAllActiveLeaveTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeDto> getLeaveTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveTypeService.getLeaveTypeById(id));
    }
} 
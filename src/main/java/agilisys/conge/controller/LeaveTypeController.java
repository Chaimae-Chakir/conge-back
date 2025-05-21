package agilisys.conge.controller;

import agilisys.conge.entity.LeaveType;
import agilisys.conge.service.LeaveTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    @GetMapping
    public ResponseEntity<List<LeaveType>> getAllActiveLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getAllActiveLeaveTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveType> getLeaveTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveTypeService.getLeaveTypeById(id));
    }
} 
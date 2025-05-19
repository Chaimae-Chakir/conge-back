package agilisys.conge.controller;

import agilisys.conge.dto.LeaveRequestDTO;
import agilisys.conge.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @GetMapping
    public ResponseEntity<List<LeaveRequestDTO>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @GetMapping("/manager-tasks")
    public ResponseEntity<List<Map<String, Object>>> getManagerTasks(
            @RequestHeader("X-Manager-Id") String managerId) {
        return ResponseEntity.ok(leaveRequestService.getManagerTasks(managerId));
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> submitLeaveRequest(
            @RequestBody LeaveRequestDTO leaveRequestDTO,
            @RequestHeader("X-Employee-Id") String employeeId,
            @RequestHeader("X-Manager-Id") String managerId) {
        return ResponseEntity.ok(leaveRequestService.submitLeaveRequest(leaveRequestDTO, employeeId, managerId));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestDTO> approveLeaveRequest(
            @PathVariable String id,
            @RequestHeader("X-Manager-Id") String managerId) {
        return ResponseEntity.ok(leaveRequestService.approveLeaveRequest(id, managerId));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestDTO> rejectLeaveRequest(
            @PathVariable String id,
            @RequestHeader("X-Manager-Id") String managerId) {
        return ResponseEntity.ok(leaveRequestService.rejectLeaveRequest(id, managerId));
    }
} 
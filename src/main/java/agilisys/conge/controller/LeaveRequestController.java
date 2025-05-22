package agilisys.conge.controller;

import agilisys.conge.dto.LeaveRequestRequestDTO;
import agilisys.conge.dto.LeaveRequestResponseDTO;
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
    public ResponseEntity<List<LeaveRequestResponseDTO>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @GetMapping("/manager-tasks")
    public ResponseEntity<List<Map<String, Object>>> getManagerTasks(
            @RequestHeader("X-Manager-Id") String managerId) {
        return ResponseEntity.ok(leaveRequestService.getManagerTasks(managerId));
    }

    @PostMapping
    public ResponseEntity<LeaveRequestResponseDTO> submitLeaveRequest(
            @RequestBody LeaveRequestRequestDTO leaveRequestRequestDTO,
            @RequestHeader("X-Employee-Name") String employeeName,
            @RequestHeader("X-Manager-Id") String managerId) {
        return ResponseEntity.ok(leaveRequestService.submitLeaveRequest(leaveRequestRequestDTO, employeeName, managerId));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestResponseDTO> approveLeaveRequest(
            @PathVariable("id") String leaveRequestId,
            @RequestHeader("X-Manager-Id") String managerId) {
        return ResponseEntity.ok(leaveRequestService.approveLeaveRequest(leaveRequestId, managerId));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestResponseDTO> rejectLeaveRequest(
            @PathVariable("id") String leaveRequestId,
            @RequestHeader("X-Manager-Id") String managerId) {
        return ResponseEntity.ok(leaveRequestService.rejectLeaveRequest(leaveRequestId, managerId));
    }
} 
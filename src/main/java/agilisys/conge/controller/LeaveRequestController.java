package agilisys.conge.controller;

import agilisys.conge.dto.LeaveRequestDTO;
import agilisys.conge.service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {
    
    private final LeaveRequestService leaveRequestService;
    
    @GetMapping
    public ResponseEntity<List<LeaveRequestDTO>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }
    
    @PostMapping
    public ResponseEntity<LeaveRequestDTO> submitLeaveRequest(
            @RequestBody LeaveRequestDTO leaveRequestDTO,
            @RequestHeader("X-Employee-Id") String employeeId) {
        leaveRequestDTO.setEmployeeId(employeeId);
        return ResponseEntity.ok(leaveRequestService.submitLeaveRequest(leaveRequestDTO));
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestDTO> approveLeaveRequest(
            @PathVariable("id") Long leaveRequestId,
            @RequestHeader("X-Manager-Id") String managerId,
            @RequestBody(required = false) String comment) {
        return ResponseEntity.ok(leaveRequestService.approveLeaveRequest(leaveRequestId, managerId, comment));
    }
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestDTO> rejectLeaveRequest(
            @PathVariable("id") Long leaveRequestId,
            @RequestHeader("X-Manager-Id") String managerId,
            @RequestBody String reason) {
        return ResponseEntity.ok(leaveRequestService.rejectLeaveRequest(leaveRequestId, managerId, reason));
    }
} 
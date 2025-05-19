package agilisys.conge.service;

import agilisys.conge.constant.LeaveConstants;
import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;
import agilisys.conge.entity.LeaveStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewLeaveRequestDelegate implements TaskListener {

    private final LeaveRequestRepository leaveRequestRepository;
    private static final String ERROR_LEAVE_REQUEST_NOT_FOUND = "Leave request not found";
    private static final String ERROR_INVALID_STATUS = "Leave request is not in PENDING status";

    @Override
    public void notify(DelegateTask delegateTask) {
        String leaveRequestId = (String) delegateTask.getVariable("leaveRequestId");
        String employeeName = (String) delegateTask.getVariable("employeeName");
        String managerId = (String) delegateTask.getVariable("manager");

        log.info("Processing review task for leave request: {} from employee: {} assigned to manager: {}", 
                leaveRequestId, employeeName, managerId);

        LeaveRequest leaveRequest = findLeaveRequest(leaveRequestId);
        validateLeaveRequestStatus(leaveRequest, leaveRequestId);
        updateTaskVariables(delegateTask, leaveRequest);
        
        log.info("Review task processed successfully for leave request: {}", leaveRequestId);
    }

    private LeaveRequest findLeaveRequest(String leaveRequestId) {
        return leaveRequestRepository.findById(Long.parseLong(leaveRequestId))
                .orElseThrow(() -> {
                    log.error("Leave request not found with id: {}", leaveRequestId);
                    return new RuntimeException(ERROR_LEAVE_REQUEST_NOT_FOUND);
                });
    }

    private void validateLeaveRequestStatus(LeaveRequest leaveRequest, String leaveRequestId) {
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            log.warn("Leave request {} is not in PENDING status anymore. Current status: {}", 
                    leaveRequestId, leaveRequest.getStatus());
            throw new RuntimeException(ERROR_INVALID_STATUS);
        }
    }

    private void updateTaskVariables(DelegateTask delegateTask, LeaveRequest leaveRequest) {
        delegateTask.setVariable("requestStatus", leaveRequest.getStatus().toString());
        delegateTask.setVariable("requestCreatedAt", leaveRequest.getCreatedAt());
    }
} 
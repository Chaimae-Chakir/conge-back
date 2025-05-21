package agilisys.conge.delegate;

import agilisys.conge.constant.ProcessConstants;
import agilisys.conge.service.LeaveManagementService;
import agilisys.conge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveRequestProcessDelegate implements JavaDelegate {
    private final LeaveManagementService leaveManagementService;
    private final NotificationService notificationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String taskType = (String) execution.getVariable("taskType");
        String leaveRequestId = (String) execution.getVariable(ProcessConstants.Variables.LEAVE_REQUEST_ID);
        
        log.info("Executing task type: {} for leave request: {}", taskType, leaveRequestId);
        
        try {
            switch(taskType) {
                case "CHECK_BALANCE":
                    leaveManagementService.validateLeaveBalance(leaveRequestId);
                    break;
                    
                case "UPDATE_BALANCE":
                    boolean isApproved = (boolean) execution.getVariable(ProcessConstants.Variables.APPROVED);
                    if (isApproved) {
                        leaveManagementService.deductLeaveBalance(leaveRequestId);
                    }
                    break;
                    
                case "NOTIFY_MANAGER":
                    notificationService.notifyManager(leaveRequestId);
                    break;
                    
                case "NOTIFY_EMPLOYEE":
                    boolean approved = (boolean) execution.getVariable(ProcessConstants.Variables.APPROVED);
                    notificationService.notifyEmployee(leaveRequestId, approved);
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown task type: " + taskType);
            }
            log.info("Successfully completed task type: {} for leave request: {}", taskType, leaveRequestId);
            
        } catch (Exception e) {
            log.error("Error executing task type: {} for leave request: {}", taskType, leaveRequestId, e);
            throw e;
        }
    }
} 
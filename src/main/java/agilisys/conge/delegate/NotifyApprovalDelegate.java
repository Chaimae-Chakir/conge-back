package agilisys.conge.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("notifyApprovalDelegate")
public class NotifyApprovalDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        Long leaveRequestId = (Long) execution.getVariable("leaveRequestId");
        String employeeId = (String) execution.getVariable("initiator");
        String managerComment = (String) execution.getVariable("managerComment");
        
        log.info("Notifying employee {} about approval of leave request {}. Comment: {}", 
                employeeId, leaveRequestId, managerComment);
        
        // TODO: Implement actual notification logic (email, notification system, etc.)
        execution.setVariable("notificationSent", true);
    }
}
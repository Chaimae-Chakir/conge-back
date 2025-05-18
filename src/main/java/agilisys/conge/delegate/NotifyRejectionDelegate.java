package agilisys.conge.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotifyRejectionDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        Long leaveRequestId = (Long) execution.getVariable("leaveRequestId");
        String employeeId = (String) execution.getVariable("initiator");
        String reason = (String) execution.getVariable("rejectionReason");
        
        log.info("Notifying employee {} about rejection of leave request {}. Reason: {}", 
                employeeId, leaveRequestId, reason);
        // TODO: Implement actual notification logic (email, notification system, etc.)
    }
}
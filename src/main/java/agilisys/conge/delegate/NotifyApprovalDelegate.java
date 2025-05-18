package agilisys.conge.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotifyApprovalDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        Long leaveRequestId = (Long) execution.getVariable("leaveRequestId");
        String employeeId = (String) execution.getVariable("initiator");
        
        log.info("Notifying employee {} about approval of leave request {}", employeeId, leaveRequestId);
        // TODO: Implement actual notification logic (email, notification system, etc.)
    }
}
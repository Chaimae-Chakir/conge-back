package agilisys.conge.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SubmitLeaveRequestDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        Long leaveRequestId = (Long) execution.getVariable("leaveRequestId");
        String employeeId = (String) execution.getVariable("initiator");
        
        log.info("Processing submit for leave request {} by employee {}", leaveRequestId, employeeId);
        // The delegate will complete automatically after this method returns
    }
} 
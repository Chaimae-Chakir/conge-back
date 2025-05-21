package agilisys.conge.delegate;

import agilisys.conge.constant.ProcessConstants;
import agilisys.conge.service.LeaveManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("checkLeaveBalanceDelegate")
@RequiredArgsConstructor
public class CheckLeaveBalanceDelegate implements JavaDelegate {

    private final LeaveManagementService leaveManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        String leaveRequestId = (String) execution.getVariable(ProcessConstants.Variables.LEAVE_REQUEST_ID);
        log.info("Vérification du solde pour la demande: {}", leaveRequestId);
        
        try {
            leaveManagementService.validateLeaveBalance(leaveRequestId);
            execution.setVariable("balanceValid", true);
            log.info("Solde suffisant pour la demande: {}", leaveRequestId);
        } catch (Exception e) {
            log.error("Solde insuffisant pour la demande: {}", leaveRequestId, e);
            execution.setVariable("balanceValid", false);
            execution.setVariable("balanceError", e.getMessage());
        }
    }
} 
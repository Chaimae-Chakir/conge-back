package agilisys.conge.delegate;

import agilisys.conge.constant.ProcessConstants;
import agilisys.conge.service.LeaveManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("checkBalanceDelegate")
@RequiredArgsConstructor
@Slf4j
public class CheckBalanceDelegate implements JavaDelegate {
    private final LeaveManagementService leaveManagementService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String leaveRequestId = (String) execution.getVariable(ProcessConstants.LEAVE_REQUEST_ID);
        log.info("Checking balance for leave request: {}", leaveRequestId);
        leaveManagementService.validateLeaveBalance(leaveRequestId);
    }
}

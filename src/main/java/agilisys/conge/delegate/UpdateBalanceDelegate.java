package agilisys.conge.delegate;

import agilisys.conge.constant.ProcessConstants;
import agilisys.conge.service.LeaveManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("updateBalanceDelegate")
@RequiredArgsConstructor
@Slf4j
public class UpdateBalanceDelegate implements JavaDelegate {
    private final LeaveManagementService leaveManagementService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String leaveRequestId = (String) execution.getVariable(ProcessConstants.LEAVE_REQUEST_ID);
        log.info("Updating balance for approved leave request: {}", leaveRequestId);
        leaveManagementService.deductLeaveBalance(leaveRequestId);
    }
}

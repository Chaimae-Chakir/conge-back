package agilisys.conge.delegate;
import agilisys.conge.constant.ProcessConstants;
import agilisys.conge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("notifyEmployeeDelegate")
@RequiredArgsConstructor
@Slf4j
public class NotifyEmployeeDelegate implements JavaDelegate {
    private final NotificationService notificationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String leaveRequestId = (String) execution.getVariable(ProcessConstants.LEAVE_REQUEST_ID);
        Boolean approved = (Boolean) execution.getVariable(ProcessConstants.APPROVED);
        if (approved == null) {
            String taskDefinitionKey = execution.getCurrentActivityId();
            approved = taskDefinitionKey.contains("Approved");
        }
        log.info("Notifying employee for leave request: {} (approved: {})", leaveRequestId, approved);
        notificationService.notifyEmployee(leaveRequestId, approved);
    }
}

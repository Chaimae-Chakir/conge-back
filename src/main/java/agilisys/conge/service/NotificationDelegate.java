package agilisys.conge.service;

import agilisys.conge.constant.LeaveConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDelegate implements JavaDelegate {

    private static final String TASK_APPROVAL = "Approval";
    private static final String TASK_REJECTION = "Rejection";
    private static final String TASK_INITIAL_REJECTION = "Initial Rejection";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String employeeName = (String) execution.getVariable("employeeName");
        String managerId = (String) execution.getVariable("manager");
        String taskName = execution.getCurrentActivityName();
        String message = buildNotificationMessage(execution, taskName);

        log.info("Sending notification for task: {} to employee: {} and manager: {}", 
                taskName, employeeName, managerId);

        sendNotification(taskName, employeeName, message);
        updateExecutionVariables(execution, message);
        
        log.info("Notification sent successfully for task: {}", taskName);
    }

    private void sendNotification(String taskName, String employeeName, String message) {
        if (taskName.contains(TASK_APPROVAL)) {
            log.info("Sending approval notification to employee: {}", employeeName);
        } else if (taskName.contains(TASK_REJECTION)) {
            log.info("Sending rejection notification to employee: {}", employeeName);
        } else if (taskName.contains(TASK_INITIAL_REJECTION)) {
            log.info("Sending initial rejection notification to employee: {}", employeeName);
        }
        log.info("Message: {}", message);
    }

    private void updateExecutionVariables(DelegateExecution execution, String message) {
        execution.setVariable("notificationMessage", message);
        execution.setVariable("notificationSent", true);
    }

    private String buildNotificationMessage(DelegateExecution execution, String taskName) {
        String employeeName = (String) execution.getVariable("employeeName");
        LocalDate startDate = (LocalDate) execution.getVariable("startDate");
        LocalDate endDate = (LocalDate) execution.getVariable("endDate");
        String reason = (String) execution.getVariable("reason");
        String validationMessage = (String) execution.getVariable("validationMessage");

        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(employeeName).append(",\n\n");

        if (taskName.contains(TASK_APPROVAL)) {
            appendApprovalMessage(message, startDate, endDate, reason);
        } else if (taskName.contains(TASK_REJECTION)) {
            appendRejectionMessage(message, startDate, endDate, reason);
        } else if (taskName.contains(TASK_INITIAL_REJECTION)) {
            appendInitialRejectionMessage(message, validationMessage);
        }

        return message.toString();
    }

    private void appendApprovalMessage(StringBuilder message, LocalDate startDate, LocalDate endDate, String reason) {
        message.append("Your leave request has been approved.\n");
        message.append("Details:\n");
        appendDateDetails(message, startDate, endDate);
        message.append("- Reason: ").append(reason).append("\n\n");
        message.append("Enjoy your leave!");
    }

    private void appendRejectionMessage(StringBuilder message, LocalDate startDate, LocalDate endDate, String reason) {
        message.append("Your leave request has been rejected.\n");
        message.append("Details:\n");
        appendDateDetails(message, startDate, endDate);
        message.append("- Reason: ").append(reason).append("\n\n");
        message.append("Please contact your manager for more information.");
    }

    private void appendInitialRejectionMessage(StringBuilder message, String validationMessage) {
        message.append("Your leave request could not be processed.\n");
        message.append("Reason: ").append(validationMessage).append("\n\n");
        message.append("Please submit a new request with valid information.");
    }

    private void appendDateDetails(StringBuilder message, LocalDate startDate, LocalDate endDate) {
        if (startDate != null) {
            message.append("- Start Date: ").append(startDate.format(DATE_FORMATTER)).append("\n");
        }
        if (endDate != null) {
            message.append("- End Date: ").append(endDate.format(DATE_FORMATTER)).append("\n");
        }
    }
} 
package agilisys.conge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String employeeName = (String) execution.getVariable("employeeName");
        String managerId = (String) execution.getVariable("manager");
        String taskName = execution.getCurrentActivityName();
        String message = buildNotificationMessage(execution, taskName);

        log.info("Sending notification for task: {} to employee: {} and manager: {}", 
                taskName, employeeName, managerId);

        // Ici, vous pourriez intégrer un service d'envoi d'emails ou de notifications
        // Par exemple : emailService.sendEmail(employeeName, message);
        
        // Pour l'instant, on simule l'envoi avec des logs
        if (taskName.contains("Approval")) {
            log.info("Sending approval notification to employee: {}", employeeName);
            log.info("Message: {}", message);
        } else if (taskName.contains("Rejection")) {
            log.info("Sending rejection notification to employee: {}", employeeName);
            log.info("Message: {}", message);
        } else if (taskName.contains("Initial Rejection")) {
            log.info("Sending initial rejection notification to employee: {}", employeeName);
            log.info("Message: {}", message);
        }

        // Stocker le message dans les variables du processus
        execution.setVariable("notificationMessage", message);
        execution.setVariable("notificationSent", true);
        
        log.info("Notification sent successfully for task: {}", taskName);
    }

    private String buildNotificationMessage(DelegateExecution execution, String taskName) {
        String employeeName = (String) execution.getVariable("employeeName");
        LocalDate startDate = (LocalDate) execution.getVariable("startDate");
        LocalDate endDate = (LocalDate) execution.getVariable("endDate");
        String reason = (String) execution.getVariable("reason");
        String validationMessage = (String) execution.getVariable("validationMessage");

        String startDateStr = startDate != null ? startDate.toString() : "";
        String endDateStr = endDate != null ? endDate.toString() : "";

        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(employeeName).append(",\n\n");

        if (taskName.contains("Approval")) {
            message.append("Your leave request has been approved.\n");
            message.append("Details:\n");
            message.append("- Start Date: ").append(startDateStr).append("\n");
            message.append("- End Date: ").append(endDateStr).append("\n");
            message.append("- Reason: ").append(reason).append("\n\n");
            message.append("Enjoy your leave!");
        } else if (taskName.contains("Rejection")) {
            message.append("Your leave request has been rejected.\n");
            message.append("Details:\n");
            message.append("- Start Date: ").append(startDateStr).append("\n");
            message.append("- End Date: ").append(endDateStr).append("\n");
            message.append("- Reason: ").append(reason).append("\n\n");
            message.append("Please contact your manager for more information.");
        } else if (taskName.contains("Initial Rejection")) {
            message.append("Your leave request could not be processed.\n");
            message.append("Reason: ").append(validationMessage).append("\n\n");
            message.append("Please submit a new request with valid information.");
        }

        return message.toString();
    }
} 
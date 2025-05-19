package agilisys.conge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidateLeaveRequestDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String employeeName = (String) execution.getVariable("employeeName");
        LocalDate startDate = (LocalDate) execution.getVariable("startDate");
        LocalDate endDate = (LocalDate) execution.getVariable("endDate");
        String reason = (String) execution.getVariable("reason");

        log.info("Validating leave request for employee: {} from {} to {}", 
                employeeName, startDate, endDate);

        // Vérifier que la date de début n'est pas dans le passé
        if (startDate.isBefore(LocalDate.now())) {
            log.warn("Start date {} is in the past", startDate);
            execution.setVariable("isValid", false);
            execution.setVariable("validationMessage", "Start date cannot be in the past");
            return;
        }

        // Vérifier que la date de fin n'est pas avant la date de début
        if (endDate.isBefore(startDate)) {
            log.warn("End date {} is before start date {}", endDate, startDate);
            execution.setVariable("isValid", false);
            execution.setVariable("validationMessage", "End date must be after start date");
            return;
        }

        // Vérifier que la durée ne dépasse pas 30 jours
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 30) {
            log.warn("Leave duration {} days exceeds maximum allowed (30 days)", daysBetween);
            execution.setVariable("isValid", false);
            execution.setVariable("validationMessage", "Leave duration cannot exceed 30 days");
            return;
        }

        // Vérifier que la raison n'est pas vide
        if (reason == null || reason.trim().isEmpty()) {
            log.warn("Reason is empty");
            execution.setVariable("isValid", false);
            execution.setVariable("validationMessage", "Reason is required");
            return;
        }

        // Si toutes les validations passent
        execution.setVariable("isValid", true);
        execution.setVariable("validationMessage", "Request is valid");
        execution.setVariable("duration", daysBetween);
        
        log.info("Leave request validation successful for employee: {}", employeeName);
    }
} 
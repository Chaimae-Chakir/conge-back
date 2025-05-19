package agilisys.conge.service;

import agilisys.conge.constant.LeaveConstants;
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

        if (!validateStartDate(execution, startDate) ||
            !validateEndDate(execution, startDate, endDate) ||
            !validateDuration(execution, startDate, endDate) ||
            !validateReason(execution, reason)) {
            return;
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        execution.setVariable("isValid", true);
        execution.setVariable("validationMessage", LeaveConstants.VALID);
        execution.setVariable("duration", daysBetween);
        
        log.info("Leave request validation successful for employee: {}", employeeName);
    }

    private boolean validateStartDate(DelegateExecution execution, LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now())) {
            log.warn("Start date {} is in the past", startDate);
            setInvalidRequest(execution, LeaveConstants.PAST_DATE);
            return false;
        }
        return true;
    }

    private boolean validateEndDate(DelegateExecution execution, LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            log.warn("End date {} is before start date {}", endDate, startDate);
            setInvalidRequest(execution, LeaveConstants.END_BEFORE_START);
            return false;
        }
        return true;
    }

    private boolean validateDuration(DelegateExecution execution, LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > LeaveConstants.MAX_LEAVE_DAYS) {
            log.warn("Leave duration {} days exceeds maximum allowed ({} days)", 
                    daysBetween, LeaveConstants.MAX_LEAVE_DAYS);
            setInvalidRequest(execution, LeaveConstants.MAX_DURATION);
            return false;
        }
        return true;
    }

    private boolean validateReason(DelegateExecution execution, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            log.warn("Reason is empty");
            setInvalidRequest(execution, LeaveConstants.EMPTY_REASON);
            return false;
        }
        return true;
    }

    private void setInvalidRequest(DelegateExecution execution, String message) {
        execution.setVariable("isValid", false);
        execution.setVariable("validationMessage", message);
    }
} 
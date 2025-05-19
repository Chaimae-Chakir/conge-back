package agilisys.conge.service;

import agilisys.conge.constant.LeaveConstants;
import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.entity.LeaveStatus;
import agilisys.conge.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckAvailableDaysDelegate implements JavaDelegate {
    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String employeeName = (String) execution.getVariable("employeeName");
        LocalDate startDate = (LocalDate) execution.getVariable("startDate");
        LocalDate endDate = (LocalDate) execution.getVariable("endDate");
        long requestedDays = (long) execution.getVariable("duration");

        log.info("Checking available leave days for employee: {} requesting {} days", 
                employeeName, requestedDays);

        long takenDays = calculateTakenDays(employeeName);
        long availableDays = calculateAvailableDays(takenDays);

        log.info("Employee {} has {} days available out of {} annual days", 
                employeeName, availableDays, LeaveConstants.ANNUAL_LEAVE_DAYS);

        if (!validateAvailableDays(execution, employeeName, requestedDays, availableDays)) {
            return;
        }

        setValidRequest(execution, availableDays, takenDays);
        log.info("Leave days check successful for employee: {}", employeeName);
    }

    private long calculateTakenDays(String employeeName) {
        List<LeaveRequest> approvedRequests = leaveRequestRepository
                .findByEmployeeNameAndStatus(employeeName, LeaveStatus.APPROVED);

        return approvedRequests.stream()
                .mapToLong(request -> ChronoUnit.DAYS.between(
                        request.getStartDate(),
                        request.getEndDate()))
                .sum();
    }

    private long calculateAvailableDays(long takenDays) {
        return LeaveConstants.ANNUAL_LEAVE_DAYS - takenDays;
    }

    private boolean validateAvailableDays(DelegateExecution execution, String employeeName, 
            long requestedDays, long availableDays) {
        if (requestedDays > availableDays) {
            log.warn("Employee {} does not have enough leave days. Requested: {}, Available: {}", 
                    employeeName, requestedDays, availableDays);
            setInvalidRequest(execution, String.format(LeaveConstants.INSUFFICIENT_DAYS, 
                    requestedDays, availableDays));
            return false;
        }
        return true;
    }

    private void setValidRequest(DelegateExecution execution, long availableDays, long takenDays) {
        execution.setVariable("isValid", true);
        execution.setVariable("validationMessage", LeaveConstants.SUCCESS);
        execution.setVariable("availableDays", availableDays);
        execution.setVariable("takenDays", takenDays);
    }

    private void setInvalidRequest(DelegateExecution execution, String message) {
        execution.setVariable("isValid", false);
        execution.setVariable("validationMessage", message);
    }
} 
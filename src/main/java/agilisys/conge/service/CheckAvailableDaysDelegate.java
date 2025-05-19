package agilisys.conge.service;

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
    private static final int ANNUAL_LEAVE_DAYS = 25;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String employeeName = (String) execution.getVariable("employeeName");
        LocalDate startDate = (LocalDate) execution.getVariable("startDate");
        LocalDate endDate = (LocalDate) execution.getVariable("endDate");
        long requestedDays = (long) execution.getVariable("duration");

        log.info("Checking available leave days for employee: {} requesting {} days", 
                employeeName, requestedDays);

        // Récupérer toutes les demandes de congé approuvées pour l'employé
        List<LeaveRequest> approvedRequests = leaveRequestRepository
                .findByEmployeeNameAndStatus(employeeName, LeaveStatus.APPROVED);

        // Calculer les jours déjà pris
        long takenDays = approvedRequests.stream()
                .mapToLong(request -> ChronoUnit.DAYS.between(
                        request.getStartDate(),
                        request.getEndDate()))
                .sum();

        // Calculer les jours disponibles
        long availableDays = ANNUAL_LEAVE_DAYS - takenDays;

        log.info("Employee {} has {} days available out of {} annual days", 
                employeeName, availableDays, ANNUAL_LEAVE_DAYS);

        // Vérifier si l'employé a assez de jours disponibles
        if (requestedDays > availableDays) {
            log.warn("Employee {} does not have enough leave days. Requested: {}, Available: {}", 
                    employeeName, requestedDays, availableDays);
            execution.setVariable("isValid", false);
            execution.setVariable("validationMessage", 
                    String.format("Not enough leave days available. Requested: %d, Available: %d", 
                            requestedDays, availableDays));
            return;
        }

        // Si la validation passe
        execution.setVariable("isValid", true);
        execution.setVariable("validationMessage", "Enough leave days available");
        execution.setVariable("availableDays", availableDays);
        execution.setVariable("takenDays", takenDays);
        
        log.info("Leave days check successful for employee: {}", employeeName);
    }
} 
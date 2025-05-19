package agilisys.conge.service;

import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateLeaveBalanceDelegate implements JavaDelegate {

    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {
        String leaveRequestId = (String) execution.getVariable("leaveRequestId");
        String employeeName = (String) execution.getVariable("employeeName");
        long duration = (long) execution.getVariable("duration");
        long availableDays = (long) execution.getVariable("availableDays");

        log.info("Updating leave balance for employee: {} for request: {}", 
                employeeName, leaveRequestId);

        // Récupérer la demande de congé
        LeaveRequest leaveRequest = leaveRequestRepository.findById(Long.parseLong(leaveRequestId))
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        // Calculer le nouveau solde
        long newBalance = availableDays - duration;

        // Mettre à jour les variables du processus
        execution.setVariable("newBalance", newBalance);
        execution.setVariable("previousBalance", availableDays);
        execution.setVariable("daysTaken", duration);

        // Ici, vous pourriez mettre à jour une table de solde de congés
        // Par exemple : leaveBalanceRepository.updateBalance(employeeName, newBalance);

        log.info("Leave balance updated for employee: {}. Previous: {}, Taken: {}, New: {}", 
                employeeName, availableDays, duration, newBalance);
    }
} 
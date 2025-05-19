package agilisys.conge.service;

import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;
import agilisys.conge.entity.LeaveStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewLeaveRequestDelegate implements TaskListener {

    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public void notify(DelegateTask delegateTask) {
        String leaveRequestId = (String) delegateTask.getVariable("leaveRequestId");
        String employeeName = (String) delegateTask.getVariable("employeeName");
        String managerId = (String) delegateTask.getVariable("manager");

        log.info("Processing review task for leave request: {} from employee: {} assigned to manager: {}", 
                leaveRequestId, employeeName, managerId);

        // Récupérer la demande de congé
        LeaveRequest leaveRequest = leaveRequestRepository.findById(Long.parseLong(leaveRequestId))
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        // Vérifier si la demande est toujours en attente
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            log.warn("Leave request {} is not in PENDING status anymore. Current status: {}", 
                    leaveRequestId, leaveRequest.getStatus());
            throw new RuntimeException("Leave request is not in PENDING status");
        }

        // Ajouter des variables supplémentaires si nécessaire
        delegateTask.setVariable("requestStatus", leaveRequest.getStatus().toString());
        delegateTask.setVariable("requestCreatedAt", leaveRequest.getCreatedAt());
        
        log.info("Review task processed successfully for leave request: {}", leaveRequestId);
    }
} 
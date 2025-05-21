package agilisys.conge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void notifyManager(String leaveRequestId) {
        // TODO: Implement email notification to manager
        log.info("Notification sent to manager for leave request: {}", leaveRequestId);
    }

    public void notifyEmployee(String leaveRequestId, boolean approved) {
        // TODO: Implement email notification to employee
        log.info("Notification sent to employee for leave request: {} - Status: {}", 
                leaveRequestId, approved ? "Approved" : "Rejected");
    }
} 
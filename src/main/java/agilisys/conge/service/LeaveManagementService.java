package agilisys.conge.service;

import agilisys.conge.entity.*;
import agilisys.conge.exception.LeaveRequestException;
import agilisys.conge.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveManagementService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Transactional(readOnly = true)
    public void validateLeaveBalance(String leaveRequestId) {
        LeaveRequest leaveRequest = findLeaveRequest(leaveRequestId);
        LeaveBalance balance = findLeaveBalance(leaveRequest.getEmployee(), leaveRequest.getLeaveType());

        if (!balance.hasEnoughDays(leaveRequest.getNumberOfDays())) {
            throw new LeaveRequestException.InsufficientBalanceException(
                "Insufficient leave balance. Available: " + balance.getAvailableDays() + 
                ", Requested: " + leaveRequest.getNumberOfDays());
        }
    }

    @Transactional
    public void deductLeaveBalance(String leaveRequestId) {
        LeaveRequest leaveRequest = findLeaveRequest(leaveRequestId);
        LeaveBalance balance = findLeaveBalance(leaveRequest.getEmployee(), leaveRequest.getLeaveType());
        
        balance.deductDays(leaveRequest.getNumberOfDays());
        leaveBalanceRepository.save(balance);
    }

    private LeaveRequest findLeaveRequest(String leaveRequestId) {
        return leaveRequestRepository.findById(Long.parseLong(leaveRequestId))
                .orElseThrow(() -> new LeaveRequestException.LeaveRequestNotFoundException(
                    "Leave request not found: " + leaveRequestId));
    }

    private LeaveBalance findLeaveBalance(Employee employee, LeaveType leaveType) {
        return leaveBalanceRepository.findByEmployeeAndLeaveTypeAndYear(
                employee, 
                leaveType, 
                Year.now().getValue())
            .orElseThrow(() -> new LeaveRequestException.LeaveBalanceNotFoundException(
                "No leave balance found for employee: " + employee.getId() + 
                " and leave type: " + leaveType.getId()));
    }
} 
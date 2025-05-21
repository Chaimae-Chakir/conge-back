package agilisys.conge.repository;

import agilisys.conge.entity.Employee;
import agilisys.conge.entity.LeaveBalance;
import agilisys.conge.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByEmployeeAndLeaveTypeAndYear(Employee employee, LeaveType leaveType, Integer year);
    
    List<LeaveBalance> findByEmployeeAndYear(Employee employee, Integer year);
    
    boolean existsByEmployeeAndLeaveTypeAndYear(Employee employee, LeaveType leaveType, Integer year);
} 
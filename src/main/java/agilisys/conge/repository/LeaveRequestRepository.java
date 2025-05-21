package agilisys.conge.repository;

import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.entity.LeaveStatus;
import agilisys.conge.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeAndStatus(Employee employee, LeaveStatus status);
} 
package agilisys.conge.service;

import agilisys.conge.entity.LeaveType;
import agilisys.conge.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public List<LeaveType> getAllActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrue();
    }

    public LeaveType getLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type de congé non trouvé avec l'id: " + id));
    }
} 
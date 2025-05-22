package agilisys.conge.service;

import agilisys.conge.dto.LeaveTypeDto;
import agilisys.conge.entity.LeaveType;
import agilisys.conge.mapper.LeaveTypeMapper;
import agilisys.conge.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LeaveTypeService {
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper  leaveTypeMapper;

    public List<LeaveTypeDto> getAllActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrue().stream().map(leaveTypeMapper::toDto).toList();
    }

    public LeaveTypeDto getLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id).map(leaveTypeMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Type de congé non trouvé avec l'id: " + id));
    }
} 
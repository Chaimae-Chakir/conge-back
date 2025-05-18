package agilisys.conge.service;

import lombok.RequiredArgsConstructor;
import agilisys.conge.dto.LeaveRequestDTO;
import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.entity.LeaveStatus;
import agilisys.conge.mapper.LeaveRequestMapper;
import agilisys.conge.repository.LeaveRequestRepository;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final LeaveRequestMapper leaveRequestMapper;
    private final LeaveRequestRepository leaveRequestRepository;

    @Transactional
    public LeaveRequestDTO submitLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(leaveRequestDTO);
        leaveRequest.setStatus(LeaveStatus.PENDING);
        
        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("leaveRequestId", savedRequest.getId());
        variables.put("initiator", leaveRequestDTO.getEmployeeId());
        variables.put("startDate", savedRequest.getStartDate());
        variables.put("endDate", savedRequest.getEndDate());
        variables.put("reason", savedRequest.getReason());
        
        runtimeService.startProcessInstanceByKey("leaveRequestProcess", variables);
        
        return leaveRequestMapper.toDto(savedRequest);
    }
    
    @Transactional
    public LeaveRequestDTO approveLeaveRequest(Long leaveRequestId, String managerId, String comment) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setManagerComment(comment);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", true);
        variables.put("managerComment", comment);
        
        String taskId = taskService.createTaskQuery()
                .processVariableValueEquals("leaveRequestId", leaveRequestId)
                .taskDefinitionKey("reviewLeaveRequest")
                .singleResult()
                .getId();
        
        taskService.complete(taskId, variables);
        
        return leaveRequestMapper.toDto(leaveRequestRepository.save(leaveRequest));
    }
    
    @Transactional
    public LeaveRequestDTO rejectLeaveRequest(Long leaveRequestId, String managerId, String reason) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setManagerComment(reason);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", false);
        variables.put("rejectionReason", reason);
        
        String taskId = taskService.createTaskQuery()
                .processVariableValueEquals("leaveRequestId", leaveRequestId)
                .taskDefinitionKey("reviewLeaveRequest")
                .singleResult()
                .getId();
        
        taskService.complete(taskId, variables);
        
        return leaveRequestMapper.toDto(leaveRequestRepository.save(leaveRequest));
    }

    public List<LeaveRequestDTO> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(leaveRequestMapper::toDto)
                .toList();
    }

    public LeaveRequestDTO getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .map(leaveRequestMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
    }
}
package agilisys.conge.service;

import agilisys.conge.constant.ProcessConstants;
import agilisys.conge.dto.LeaveRequestRequestDTO;
import agilisys.conge.dto.LeaveRequestResponseDTO;
import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.entity.LeaveStatus;
import agilisys.conge.entity.Employee;
import agilisys.conge.entity.LeaveType;
import agilisys.conge.exception.LeaveRequestException;
import agilisys.conge.mapper.LeaveRequestMapper;
import agilisys.conge.repository.LeaveRequestRepository;
import agilisys.conge.repository.EmployeeRepository;
import agilisys.conge.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final CamundaProcessService camundaProcessService;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public List<LeaveRequestResponseDTO> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(leaveRequestMapper::toResponseDto)
                .toList();
    }

    public List<Map<String, Object>> getManagerTasks(String managerId) {
        return camundaProcessService.getManagerTasks(managerId).stream()
                .map(task -> {
                    Map<String, Object> taskInfo = new HashMap<>();
                    taskInfo.put(ProcessConstants.TASK_ID, task.getId());
                    taskInfo.put(ProcessConstants.PROCESS_INSTANCE_ID, task.getProcessInstanceId());
                    taskInfo.put(ProcessConstants.CREATED_DATE, task.getCreateTime());
                    taskInfo.put(ProcessConstants.DUE_DATE, task.getDueDate());
                    Map<String, Object> variables = camundaProcessService.getTaskVariables(task.getId());
                    taskInfo.putAll(variables);
                    return taskInfo;
                })
                .toList();
    }

    @Transactional
    public LeaveRequestResponseDTO submitLeaveRequest(@Valid LeaveRequestRequestDTO requestDTO, String employeeName, String managerId) {
        log.info("Submitting leave request for employee: {} to manager: {}", employeeName, managerId);
        Employee employee = employeeRepository.findByName(employeeName)
                .orElseThrow(() -> new LeaveRequestException.EmployeeNotFoundException("Employee not found: " + employeeName));
        LeaveType leaveType = leaveTypeRepository.findById(requestDTO.getLeaveTypeId())
                .orElseThrow(() -> new LeaveRequestException.LeaveTypeNotFoundException("Leave type not found: " + requestDTO.getLeaveTypeId()));
        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(requestDTO);
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        Map<String, Object> variables = createProcessVariables(leaveRequest, employeeName, managerId);
        camundaProcessService.startLeaveRequestProcess(
                ProcessConstants.LEAVE_REQUEST_PROCESS,
                leaveRequest.getId().toString(),
                variables
        );

        return leaveRequestMapper.toResponseDto(leaveRequest);
    }

    @Transactional
    public LeaveRequestResponseDTO approveLeaveRequest(String leaveRequestId, String managerId) {
        return processLeaveRequest(leaveRequestId, managerId, true);
    }

    @Transactional
    public LeaveRequestResponseDTO rejectLeaveRequest(String leaveRequestId, String managerId) {
        return processLeaveRequest(leaveRequestId, managerId, false);
    }

    private Map<String, Object> createProcessVariables(LeaveRequest leaveRequest, String employeeName, String managerId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstants.LEAVE_REQUEST_ID, leaveRequest.getId().toString());
        variables.put(ProcessConstants.EMPLOYEE_NAME, employeeName);
        variables.put(ProcessConstants.MANAGER, managerId);
        variables.put(ProcessConstants.START_DATE, leaveRequest.getStartDate());
        variables.put(ProcessConstants.END_DATE, leaveRequest.getEndDate());
        variables.put(ProcessConstants.REASON, leaveRequest.getReason());
        return variables;
    }

    private LeaveRequestResponseDTO processLeaveRequest(String leaveRequestId, String managerId, boolean isApproved) {
        LeaveRequest leaveRequest = getLeaveRequestOrThrow(leaveRequestId);
        ProcessInstance processInstance = getActiveProcessInstanceOrThrow(leaveRequestId);
        Task reviewTask = getActiveReviewTaskOrThrow(processInstance.getId());
        verifyTaskAssignment(reviewTask, managerId);
        completeReviewTask(reviewTask, isApproved);
        leaveRequest = updateLeaveRequestStatus(leaveRequest, isApproved);
        return leaveRequestMapper.toResponseDto(leaveRequest);
    }

    private LeaveRequest getLeaveRequestOrThrow(String id) {
        return leaveRequestRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new LeaveRequestException.LeaveRequestNotFoundException(
                        "Leave request not found: " + id));
    }

    private ProcessInstance getActiveProcessInstanceOrThrow(String businessKey) {
        return camundaProcessService.findActiveProcessInstance(businessKey)
                .orElseThrow(() -> new LeaveRequestException.ProcessNotFoundException(
                        "No active process instance found for business key: " + businessKey));
    }

    private Task getActiveReviewTaskOrThrow(String processInstanceId) {
        return camundaProcessService.findActiveReviewTask(processInstanceId)
                .orElseThrow(() -> new LeaveRequestException.TaskNotFoundException(
                        "No active review task found for process instance: " + processInstanceId));
    }

    private void verifyTaskAssignment(Task task, String userId) {
        if (!camundaProcessService.isTaskAssignedToUser(task, userId)) {
            throw new LeaveRequestException.UnauthorizedTaskActionException(
                    "Task not assigned to user " + userId);
        }
    }

    private void completeReviewTask(Task task, boolean approved) {
        Map<String, Object> variables = Map.of(ProcessConstants.APPROVED, approved);
        camundaProcessService.completeTask(task.getId(), variables);
    }

    private LeaveRequest updateLeaveRequestStatus(LeaveRequest leaveRequest, boolean approved) {
        leaveRequest.setStatus(approved ? LeaveStatus.APPROVED : LeaveStatus.REJECTED);
        return leaveRequestRepository.save(leaveRequest);
    }

}
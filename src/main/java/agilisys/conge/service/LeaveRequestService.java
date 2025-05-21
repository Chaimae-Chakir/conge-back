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
                    taskInfo.put(ProcessConstants.TaskInfo.TASK_ID, task.getId());
                    taskInfo.put(ProcessConstants.TaskInfo.PROCESS_INSTANCE_ID, task.getProcessInstanceId());
                    taskInfo.put(ProcessConstants.TaskInfo.CREATED_DATE, task.getCreateTime());
                    taskInfo.put(ProcessConstants.TaskInfo.DUE_DATE, task.getDueDate());
                    Map<String, Object> variables = camundaProcessService.getTaskVariables(task.getId());
                    taskInfo.putAll(variables);
                    return taskInfo;
                })
                .toList();
    }

    @Transactional
    public LeaveRequestResponseDTO submitLeaveRequest(@Valid LeaveRequestRequestDTO requestDTO, String employeeName, String managerId) {
        log.info("Submitting leave request for employee: {} to manager: {}", employeeName, managerId);
        List<Employee> all = employeeRepository.findAll();
        all.forEach(employee -> log.info("Employee: {}", employee));
        // Find employee by email
        Employee employee = employeeRepository.findByName(employeeName)
                .orElseThrow(() -> new LeaveRequestException.EmployeeNotFoundException("Employee not found: " + employeeName));
        
        // Find leave type
        LeaveType leaveType = leaveTypeRepository.findById(requestDTO.getLeaveTypeId())
                .orElseThrow(() -> new LeaveRequestException.LeaveTypeNotFoundException("Leave type not found: " + requestDTO.getLeaveTypeId()));
        
        // Create and save the leave request
        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(requestDTO);
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        
        // Start the Camunda process
        Map<String, Object> variables = createProcessVariables(leaveRequest, employeeName, managerId);
        ProcessInstance processInstance = camundaProcessService.startLeaveRequestProcess(
                ProcessConstants.Processes.LEAVE_REQUEST_PROCESS,
                leaveRequest.getId().toString(),
                variables
        );

        // Log process tasks for debugging
        logProcessTasks(processInstance.getId());
        
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
        variables.put(ProcessConstants.Variables.LEAVE_REQUEST_ID, leaveRequest.getId().toString());
        variables.put(ProcessConstants.Variables.EMPLOYEE_NAME, employeeName);
        variables.put(ProcessConstants.Variables.MANAGER, managerId);
        variables.put(ProcessConstants.Variables.START_DATE, leaveRequest.getStartDate());
        variables.put(ProcessConstants.Variables.END_DATE, leaveRequest.getEndDate());
        variables.put(ProcessConstants.Variables.REASON, leaveRequest.getReason());
        return variables;
    }

    private void logProcessTasks(String processInstanceId) {
        List<Task> tasks = camundaProcessService.getManagerTasks(processInstanceId);
        if (tasks.isEmpty()) {
            log.warn("No active tasks found for process instance: {}", processInstanceId);
            return;
        }
        log.info("Active tasks for process instance {}: {}", 
                processInstanceId,
                tasks.stream()
                    .map(task -> String.format("Task[id=%s, name=%s, assignee=%s]", 
                        task.getId(), task.getName(), task.getAssignee()))
                    .collect(Collectors.joining(", ")));
    }

    private LeaveRequestResponseDTO processLeaveRequest(String leaveRequestId, String managerId, boolean isApproved) {
        log.info("Processing leave request: {} for manager: {}, approved: {}", leaveRequestId, managerId, isApproved);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(Long.parseLong(leaveRequestId))
                .orElseThrow(() -> new LeaveRequestException.LeaveRequestNotFoundException(
                        "Leave request not found: " + leaveRequestId));
        log.info("Found leave request: {}", leaveRequest);

        ProcessInstance processInstance = camundaProcessService.findActiveProcessInstance(leaveRequestId)
                .orElseThrow(() -> new LeaveRequestException.ProcessNotFoundException(
                        "No active process instance found for leave request: " + leaveRequestId));
        log.info("Found process instance: {}", processInstance.getId());

        Task reviewTask = camundaProcessService.findActiveReviewTask(processInstance.getId())
                .orElseThrow(() -> new LeaveRequestException.TaskNotFoundException(
                        "No active review task found for leave request: " + leaveRequestId));
        log.info("Found review task: {}", reviewTask.getId());

        if (!camundaProcessService.isTaskAssignedToUser(reviewTask, managerId)) {
            log.error("Task {} is not assigned to manager {}", reviewTask.getId(), managerId);
            throw new LeaveRequestException.UnauthorizedTaskActionException(
                    "This task is not assigned to the specified manager");
        }
        log.info("Task is assigned to manager: {}", managerId);

        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstants.Variables.APPROVED, isApproved);
        camundaProcessService.completeTask(reviewTask.getId(), variables);
        log.info("Completed task with variables: {}", variables);

        leaveRequest.setStatus(isApproved ? LeaveStatus.APPROVED : LeaveStatus.REJECTED);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        log.info("Updated leave request status to: {}", leaveRequest.getStatus());

        return leaveRequestMapper.toResponseDto(leaveRequest);
    }
}
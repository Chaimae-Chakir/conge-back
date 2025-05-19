package agilisys.conge.service;

import agilisys.conge.dto.LeaveRequestDTO;
import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.entity.LeaveStatus;
import agilisys.conge.mapper.LeaveRequestMapper;
import agilisys.conge.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    private static final String PROCESS_DEFINITION_KEY = "leaveRequestProcess";
    private static final String REVIEW_TASK_NAME = "Review Leave Request";

    public List<LeaveRequestDTO> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(leaveRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getManagerTasks(String managerId) {
        return taskService.createTaskQuery()
                .taskAssignee(managerId)
                .taskName(REVIEW_TASK_NAME)
                .active()
                .list()
                .stream()
                .map(task -> {
                    Map<String, Object> taskInfo = new HashMap<>();
                    taskInfo.put("taskId", task.getId());
                    taskInfo.put("processInstanceId", task.getProcessInstanceId());
                    taskInfo.put("created", task.getCreateTime());
                    taskInfo.put("dueDate", task.getDueDate());
                    taskInfo.put("variables", taskService.getVariables(task.getId()));
                    return taskInfo;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveRequestDTO submitLeaveRequest(LeaveRequestDTO leaveRequestDTO, String employeeId, String managerId) {
        log.info("Submitting leave request for employee: {} to manager: {}", employeeId, managerId);
        LeaveRequest leaveRequest = leaveRequestMapper.toEntity(leaveRequestDTO);
        leaveRequest.setEmployeeId(Long.parseLong(employeeId));
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        Map<String, Object> variables = new HashMap<>();
        variables.put("leaveRequestId", leaveRequest.getId().toString());
        variables.put("employeeId", employeeId);
        variables.put("manager", managerId);
        variables.put("startDate", leaveRequest.getStartDate());
        variables.put("endDate", leaveRequest.getEndDate());
        variables.put("reason", leaveRequest.getReason());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                PROCESS_DEFINITION_KEY,
                leaveRequest.getId().toString(),
                variables
        );
        log.info("Started process instance: {} for leave request: {}", 
                processInstance.getId(), leaveRequest.getId());
        // Verify task creation
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .active()
                .list();
        log.info("Created tasks for process instance {}: {}", 
                processInstance.getId(), 
                tasks.stream()
                    .map(task -> String.format("Task[id=%s, name=%s, assignee=%s]", 
                        task.getId(), task.getName(), task.getAssignee()))
                    .collect(Collectors.joining(", ")));
        return leaveRequestMapper.toDto(leaveRequest);
    }

    @Transactional
    public LeaveRequestDTO approveLeaveRequest(String leaveRequestId, String managerId) {
        log.info("Approving leave request: {} by manager: {}", leaveRequestId, managerId);
        LeaveRequest leaveRequest = leaveRequestRepository.findById(Long.parseLong(leaveRequestId))
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        // First try to find the process instance
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(leaveRequestId)
                .active()
                .singleResult();
        if (processInstance == null) {
            throw new RuntimeException("No active process instance found for leave request: " + leaveRequestId);
        }
        log.info("Found process instance: {} for leave request: {}", processInstance.getId(), leaveRequestId);
        // List all tasks for debugging
        List<Task> allTasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .active()
                .list();
        log.info("Active tasks for process instance {}: {}", 
                processInstance.getId(),
                allTasks.stream()
                    .map(task -> String.format("Task[id=%s, name=%s, assignee=%s]", 
                        task.getId(), task.getName(), task.getAssignee()))
                    .collect(Collectors.joining(", ")));
        // Try to find the review task
        Task reviewTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskName(REVIEW_TASK_NAME)
                .active()
                .singleResult();
        if (reviewTask == null) {
            throw new RuntimeException("No active review task found for this leave request");
        }
        // Verify task assignment
        if (!managerId.equals(reviewTask.getAssignee())) {
            log.warn("Task {} is assigned to {} but manager {} is trying to complete it", 
                    reviewTask.getId(), reviewTask.getAssignee(), managerId);
            throw new RuntimeException("This task is not assigned to the specified manager");
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", true);
        taskService.complete(reviewTask.getId(), variables);
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        log.info("Leave request: {} approved successfully", leaveRequestId);
        return leaveRequestMapper.toDto(leaveRequest);
    }

    @Transactional
    public LeaveRequestDTO rejectLeaveRequest(String leaveRequestId, String managerId) {
        log.info("Rejecting leave request: {} by manager: {}", leaveRequestId, managerId);
        LeaveRequest leaveRequest = leaveRequestRepository.findById(Long.parseLong(leaveRequestId))
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        // First try to find the process instance
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(leaveRequestId)
                .active()
                .singleResult();
        if (processInstance == null) {
            throw new RuntimeException("No active process instance found for leave request: " + leaveRequestId);
        }
        log.info("Found process instance: {} for leave request: {}", processInstance.getId(), leaveRequestId);
        // List all tasks for debugging
        List<Task> allTasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .active()
                .list();
        log.info("Active tasks for process instance {}: {}", 
                processInstance.getId(),
                allTasks.stream()
                    .map(task -> String.format("Task[id=%s, name=%s, assignee=%s]", 
                        task.getId(), task.getName(), task.getAssignee()))
                    .collect(Collectors.joining(", ")));
        // Try to find the review task
        Task reviewTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskName(REVIEW_TASK_NAME)
                .active()
                .singleResult();
        if (reviewTask == null) {
            throw new RuntimeException("No active review task found for this leave request");
        }
        // Verify task assignment
        if (!managerId.equals(reviewTask.getAssignee())) {
            log.warn("Task {} is assigned to {} but manager {} is trying to complete it", 
                    reviewTask.getId(), reviewTask.getAssignee(), managerId);
            throw new RuntimeException("This task is not assigned to the specified manager");
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", false);
        taskService.complete(reviewTask.getId(), variables);
        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest = leaveRequestRepository.save(leaveRequest);
        log.info("Leave request: {} rejected successfully", leaveRequestId);
        return leaveRequestMapper.toDto(leaveRequest);
    }
}
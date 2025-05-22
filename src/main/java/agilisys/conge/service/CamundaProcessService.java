package agilisys.conge.service;

import agilisys.conge.constant.ProcessConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CamundaProcessService {
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public void startLeaveRequestProcess(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        log.info("Started process {} with business key {} and variables {}",
                processDefinitionKey, businessKey, variables);
    }

    public Optional<Task> findActiveReviewTask(String processInstanceId) {
        log.info("Searching for active review task in process instance: {}", processInstanceId);

        // List all active tasks for this process
        List<Task> allActiveTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();
        log.info("All active tasks for process {}: {}",
                processInstanceId,
                allActiveTasks.stream()
                        .map(t -> String.format("ID=%s, Name=%s, Assignee=%s",
                                t.getId(), t.getName(), t.getAssignee()))
                        .toList());

        // Search specifically for the review task using taskDefinitionKey
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(ProcessConstants.REVIEW_REQUEST)
                .active()
                .singleResult();

        if (task == null) {
            log.warn("No active review task found for process instance: {}", processInstanceId);

        } else {
            log.info("Found active review task: {} for process instance: {}",
                    task.getId(), processInstanceId);
        }
        return Optional.ofNullable(task);
    }

    public Optional<ProcessInstance> findActiveProcessInstance(String businessKey) {
        log.info("Searching for active process instance with business key: {}", businessKey);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .active()
                .singleResult();

        if (processInstance == null) {
            log.warn("No active process instance found for business key: {}", businessKey);
        } else {
            log.info("Found active process instance: {} for business key: {}",
                    processInstance.getId(), businessKey);
        }

        return Optional.ofNullable(processInstance);
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
        log.info("Completed task: {} with variables: {}", taskId, variables);
    }

    public List<Task> getManagerTasks(String managerId) {
        return taskService.createTaskQuery()
                .taskAssignee(managerId)
                .taskDefinitionKey(ProcessConstants.REVIEW_REQUEST)
                .active()
                .list();
    }

    public boolean isTaskAssignedToUser(Task task, String userId) {
        return userId.equals(task.getAssignee());
    }

    public Map<String, Object> getTaskVariables(String taskId) {
        return taskService.getVariables(taskId);
    }
}
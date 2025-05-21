package agilisys.conge.constant;

public final class ProcessConstants {
    private ProcessConstants() {
        // Utility class
    }

    public static final class Processes {
        private Processes() {
            // Utility class
        }

        public static final String LEAVE_REQUEST_PROCESS = "leaveRequestProcess";
    }

    public static final class Variables {
        private Variables() {
            // Utility class
        }

        public static final String LEAVE_REQUEST_ID = "leaveRequestId";
        public static final String APPROVED = "approved";
        public static final String EMPLOYEE_NAME = "employeeName";
        public static final String MANAGER = "manager";
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final String REASON = "reason";
    }

    public static final class Tasks {
        private Tasks() {
            // Utility class
        }

        public static final String REVIEW_REQUEST = "reviewLeaveRequest";
    }

    public static final class TaskInfo {
        private TaskInfo() {
            // Utility class
        }

        public static final String TASK_ID = "taskId";
        public static final String PROCESS_INSTANCE_ID = "processInstanceId";
        public static final String CREATED_DATE = "createdDate";
        public static final String DUE_DATE = "dueDate";
    }
} 
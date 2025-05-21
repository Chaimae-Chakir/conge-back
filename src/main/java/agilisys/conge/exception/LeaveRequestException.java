package agilisys.conge.exception;

public class LeaveRequestException extends RuntimeException {
    public LeaveRequestException(String message) {
        super(message);
    }

    public static class LeaveRequestNotFoundException extends LeaveRequestException {
        public LeaveRequestNotFoundException(String message) {
            super(message);
        }
    }

    public static class ProcessNotFoundException extends LeaveRequestException {
        public ProcessNotFoundException(String message) {
            super(message);
        }
    }

    public static class TaskNotFoundException extends LeaveRequestException {
        public TaskNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedTaskActionException extends LeaveRequestException {
        public UnauthorizedTaskActionException(String message) {
            super(message);
        }
    }

    public static class EmployeeNotFoundException extends LeaveRequestException {
        public EmployeeNotFoundException(String message) {
            super(message);
        }
    }

    public static class LeaveTypeNotFoundException extends LeaveRequestException {
        public LeaveTypeNotFoundException(String message) {
            super(message);
        }
    }

    public static class LeaveBalanceNotFoundException extends LeaveRequestException {
        public LeaveBalanceNotFoundException(String message) {
            super(message);
        }
    }

    public static class InsufficientBalanceException extends LeaveRequestException {
        public InsufficientBalanceException(String message) {
            super(message);
        }
    }
} 
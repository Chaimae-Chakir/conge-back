package agilisys.conge.constant;

public final class LeaveConstants {
    public static final int MAX_LEAVE_DAYS = 30;  // Maximum days for a single request
    public static final int ANNUAL_LEAVE_DAYS = 25;  // Total days available per year
    public static final String PAST_DATE = "Start date cannot be in the past";
    public static final String END_BEFORE_START = "End date must be after start date";
    public static final String MAX_DURATION = "Leave duration cannot exceed 30 days";
    public static final String EMPTY_REASON = "Reason is required";
    public static final String VALID = "Request is valid";
    public static final String INSUFFICIENT_DAYS = "Not enough leave days available. Requested: %d, Available: %d";
    public static final String SUCCESS = "Enough leave days available";
} 
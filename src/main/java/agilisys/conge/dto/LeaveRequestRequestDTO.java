package agilisys.conge.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LeaveRequestRequestDTO {
    @NotNull(message = "Leave type is required")
    private Long leaveTypeId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;

    @AssertTrue(message = "End date must be after or equal to start date")
    public boolean isEndDateValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "Leave request cannot exceed 30 days")
    public boolean isLeaveDurationValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return days <= 30;
    }
} 
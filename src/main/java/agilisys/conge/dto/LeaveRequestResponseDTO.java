package agilisys.conge.dto;

import agilisys.conge.entity.LeaveStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveRequestResponseDTO {
    private Long id;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private LocalDateTime createdAt;
} 
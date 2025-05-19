package agilisys.conge.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LeaveRequestRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
} 
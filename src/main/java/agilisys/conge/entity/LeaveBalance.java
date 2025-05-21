package agilisys.conge.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "leave_balances")
@ToString(exclude = {"employee", "leaveType"})
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;
    
    @Column(nullable = false)
    private Integer availableDays;
    
    @Column(nullable = false)
    private Integer usedDays = 0;
    
    @Column(nullable = false)
    private Integer year;
    
    public boolean hasEnoughDays(int requestedDays) {
        return availableDays >= requestedDays;
    }
    
    public void deductDays(int days) {
        if (!hasEnoughDays(days)) {
            throw new IllegalStateException("Insufficient leave balance");
        }
        availableDays -= days;
        usedDays += days;
    }
    
    public void refundDays(int days) {
        availableDays += days;
        usedDays -= days;
    }
} 
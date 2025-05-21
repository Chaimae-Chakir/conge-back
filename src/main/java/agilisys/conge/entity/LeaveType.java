package agilisys.conge.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Table(name = "leave_types")
@ToString(exclude = {"leaveBalances", "leaveRequests"})
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private Integer defaultDays;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @OneToMany(mappedBy = "leaveType")
    private List<LeaveBalance> leaveBalances;
    
    @OneToMany(mappedBy = "leaveType")
    private List<LeaveRequest> leaveRequests;
} 
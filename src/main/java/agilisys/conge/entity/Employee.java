package agilisys.conge.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Table(name = "employees")
@ToString(exclude = {"leaveBalances", "leaveRequests"})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @OneToMany(mappedBy = "employee")
    private List<LeaveBalance> leaveBalances;
    
    @OneToMany(mappedBy = "employee")
    private List<LeaveRequest> leaveRequests;
} 
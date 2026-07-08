package com.modi.leave_management_system.entity;

import com.modi.leave_management_system.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveRequestId;
    @ManyToOne
    @JoinColumn(name="employee_id", nullable = false)
    private Employee employee;
    @ManyToOne
    @JoinColumn(name="leave_type_id", nullable = false)
    private LeaveType leaveType;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    private double numberOfDays; //calculated, excluding weekends
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
    private String reason;
    private String managerComment;
    @ManyToOne
    @JoinColumn(name="approved_by")
    private Employee approvedBy;
    @Column(updatable = false)
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    @PrePersist // it runs automatically before an INSERT is run.
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }
    @PreUpdate //runs automatically before UPDATE is run.
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

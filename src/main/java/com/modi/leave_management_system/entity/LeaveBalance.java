package com.modi.leave_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "leave_balances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "leave_type_id", "year"})
)
public class LeaveBalance {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long leaveBalanceId;
     @ManyToOne
     @JoinColumn(name="employee_id", nullable = false)
    private Employee employee;
     @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;
     private int year;
     private double totalDays; //allocated at year start
     private double usedDays;
     private double pendingDays; //days in PENDING requests like employee requests for a leave and its pending for approval
    //derived: available leaves = total - used - pending
     @Transient //transient tells the db to not map these to a column, its a computed column
    public double getAvailableDays() {
        return totalDays - usedDays - pendingDays;
    }

}

package com.modi.leave_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="leave_types")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveTypeId;
    @Column(nullable = false, unique = true)
    private String leaveTypeName; //Casual,Sick,Vacation,Earned
    private int defaultDaysPerYear;
    private boolean carryForwardAllowed;

}

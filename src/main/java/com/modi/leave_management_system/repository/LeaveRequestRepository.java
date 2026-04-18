package com.modi.leave_management_system.repository;

import com.modi.leave_management_system.entity.Employee;
import com.modi.leave_management_system.entity.LeaveRequest;
import com.modi.leave_management_system.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(Employee employee);
    List<LeaveRequest> findByEmployeeAndStatus(Employee employee, LeaveStatus status);
    // manager sees all pending requests from their team
    @Query("SELECT lr FROM LeaveRequest lr " +
            "WHERE lr.employee.manager = :manager " +
            "AND lr.status = 'PENDING'")
    List<LeaveRequest> findPendingRequestsByManager(@Param("manager") Employee manager);

    // manager sees full history of their team (all statuses)
    @Query("SELECT lr FROM LeaveRequest lr " +
            "WHERE lr.employee.manager = :manager")
    List<LeaveRequest> findAllRequestsByManager(@Param("manager") Employee manager);

    // overlap check — before approving, ensure no existing approved leave
    // covers the same date range for this employee
    @Query("SELECT lr FROM LeaveRequest lr " +
            "WHERE lr.employee = :employee " +
            "AND lr.status IN ('PENDING', 'APPROVED') " +
            "AND lr.startDate <= :endDate " +
            "AND lr.endDate >= :startDate")
    List<LeaveRequest> findOverlappingRequests(
            @Param("employee") Employee employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}



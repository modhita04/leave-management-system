package com.modi.leave_management_system.repository;

import com.modi.leave_management_system.entity.Employee;
import com.modi.leave_management_system.entity.LeaveBalance;
import com.modi.leave_management_system.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByEmployeeAndLeaveTypeAndYear(Employee employee, LeaveType leaveType, int year);
    //most used query-check one employee balance for specific type and year
    List<LeaveBalance> findByEmployeeAndYear(Employee employee, int year);
    //all balances for an employee in a given year(show all times leave types at once)
    @Query("SELECT lb FROM LeaveBalance lb " +
            "WHERE lb.employee = :employee " +
            "AND lb.year = :year " +
            "AND (lb.totalDays - lb.usedDays - lb.pendingDays) > 0")
    //custom JPQL - find balances where available days >0
    // useful for filtering leave types the employees can still apply for
    List<LeaveBalance> findAvailableBalances(
            @Param("employee") Employee employee,
            @Param("year") int year);

}

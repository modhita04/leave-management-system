package com.modi.leave_management_system.repository;

import com.modi.leave_management_system.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    Optional<LeaveType> findByLeaveTypeName(String leaveTypeName); //lookup by name: casual, sick
    boolean existsByLeaveTypeName(String leaveTypeName); //check for duplicates before creating new leave type
}


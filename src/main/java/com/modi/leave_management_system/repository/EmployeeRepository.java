package com.modi.leave_management_system.repository;

import com.modi.leave_management_system.entity.Employee;
import com.modi.leave_management_system.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmpEmail(String empEmail); //login/auth lookup
    List<Employee> findByManager(Employee manager); //finding employees under a manager
    List<Employee> findByRole(Role role); //find all employees by role aka look for all managers
    boolean existsByEmpEmail(String empEmail); //check if email already exists


}

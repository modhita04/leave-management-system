package com.modi.leave_management_system.exception;

public class OverlappingLeaveException extends RuntimeException {
    public OverlappingLeaveException(String message) {
        super(message);
    }
}

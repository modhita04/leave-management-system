package com.modi.leave_management_system.exception;
import com.modi.leave_management_system.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //handles insufficient leave balance
    @ExceptionHandler(InsufficientLeaveException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientLeave(
            InsufficientLeaveException ex) {

        ErrorResponse error = new ErrorResponse(
                400,
                "Insufficient Leave Balance",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    //handles leave not found
    @ExceptionHandler(LeaveNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLeaveNotFound(LeaveNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                400,
                "Leave Request Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    //handles overlapping leave dates
    @ExceptionHandler(OverlappingLeaveException.class)
    public ResponseEntity<ErrorResponse>  handleOverlappingLeave(OverlappingLeaveException ex) {
        ErrorResponse error = new ErrorResponse(
                400,
                "Overlapping leave request",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    // catches everything else — safety net
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGeneral(RuntimeException ex) {

        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

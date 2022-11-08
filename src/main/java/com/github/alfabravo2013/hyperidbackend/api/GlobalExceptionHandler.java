package com.github.alfabravo2013.hyperidbackend.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<ErrorDto> handleUsernameTaken(HttpServletRequest req, UsernameTakenException ex) {
        var body = ErrorDto.of(ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FailedAuthException.class)
    public ResponseEntity<ErrorDto> handleRuntime(HttpServletRequest req, FailedAuthException ex) {
        var body = ErrorDto.of(ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(HttpServletRequest req, AccessDeniedException ex) {
        var body = ErrorDto.of(ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(HttpServletRequest req, NotFoundException ex) {
        var body = ErrorDto.of(ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}

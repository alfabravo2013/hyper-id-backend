package com.github.alfabravo2013.hyperidbackend.api;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.alfabravo2013.hyperidbackend.exceptions.AccessDeniedException;
import com.github.alfabravo2013.hyperidbackend.exceptions.FailedAuthException;
import com.github.alfabravo2013.hyperidbackend.exceptions.NotFoundException;
import com.github.alfabravo2013.hyperidbackend.exceptions.UsernameTakenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<String> handleUsernameTaken(HttpServletRequest req, UsernameTakenException ex) {
        LOGGER.debug("Handling {} exception", ex.getClass().getSimpleName());
        var body = ErrorDto.of(ex.getMessage()).toString();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(FailedAuthException.class)
    public ResponseEntity<String> handleAuthFailure(HttpServletRequest req, FailedAuthException ex) {
        LOGGER.debug("Handling {} exception", ex.getClass().getSimpleName());
        var body = ErrorDto.of(ex.getMessage()).toString();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(HttpServletRequest req, AccessDeniedException ex) {
        LOGGER.debug("Handling {} exception", ex.getClass().getSimpleName());
        var body = ErrorDto.of(ex.getMessage()).toString();
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(HttpServletRequest req, NotFoundException ex) {
        LOGGER.debug("Handling {} exception", ex.getClass().getSimpleName());
        var body = ErrorDto.of(ex.getMessage()).toString();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        LOGGER.debug("Handling {} exception", ex.getClass().getSimpleName());
        var message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(ex.getMessage());
        var body = ErrorDto.of(message).toString();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        LOGGER.debug("Handling {} exception", ex.getClass().getSimpleName());
        var message = ex.getMessage();
        var rootCause = ex.getRootCause();
        if (rootCause instanceof UnrecognizedPropertyException) {
            message = "Unknown field: " + ((UnrecognizedPropertyException) rootCause).getPropertyName();
        }
        var body = ErrorDto.of(message).toString();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingHeader(HttpServletRequest req, MissingRequestHeaderException ex) {
        LOGGER.debug("Handling {} exception", ex.getClass().getSimpleName());
        var body = ErrorDto.of("Header 'Authorization' is empty").toString();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}

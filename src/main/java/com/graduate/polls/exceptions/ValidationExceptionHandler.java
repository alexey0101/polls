package com.graduate.polls.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validation global exception handler
 */
@ControllerAdvice
public class ValidationExceptionHandler {
    /**
     * Handle method argument not valid exception
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, List<String>> body = new HashMap<>();
        body.put("errors", ex.getBindingResult().getFieldErrors().stream().map(e -> e.getDefaultMessage()).toList());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle json parse exception
     * @param ex
     * @return
     */
    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<Object> handleJsonParseException(JsonParseException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid data");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle http message not readable exception
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid data");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violation exception
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getConstraintViolations().stream().map(e -> e.getMessage()).findFirst().orElse("Invalid data"));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle jwt exception
     * @param ex
     * @return
     */
    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<Object> handleJwtException(JwtException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid token");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle illegal argument exception
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleRegisterException(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid data");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle bad credentials exception
     * @param ex
     * @return
     */
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid credentials");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}

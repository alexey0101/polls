package com.graduate.polls.controllers;

import com.graduate.polls.requests.AuthenticationRequest;
import com.graduate.polls.requests.RegisterRequest;
import com.graduate.polls.responses.AuthenticationResponse;
import com.graduate.polls.responses.ErrorResponse;
import com.graduate.polls.service.api.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  /**
   * Register new user
   * @param request
   * @return
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(
          @Valid @RequestBody RegisterRequest request
  ) {
    try {
          return ResponseEntity.ok(service.register(request));
    } catch (Exception e) {
          return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
  }

  /**
   * Authenticate user
   * @param request
   * @return
   */
  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
          @Valid @RequestBody AuthenticationRequest request
  ) {
    try {
        return ResponseEntity.ok(service.authenticate(request));
        } catch (Exception e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }
  }
}


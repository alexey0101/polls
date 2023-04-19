package com.graduate.polls.controllers;

import com.graduate.polls.requests.AuthenticationRequest;
import com.graduate.polls.requests.RegisterRequest;
import com.graduate.polls.responses.AuthenticationResponse;
import com.graduate.polls.service.api.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<AuthenticationResponse> register(
          @Valid @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }

  /**
   * Authenticate user
   * @param request
   * @return
   */
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @Valid @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }
}

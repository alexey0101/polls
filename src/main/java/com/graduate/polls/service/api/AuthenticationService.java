package com.graduate.polls.service.api;

import com.graduate.polls.requests.AuthenticationRequest;
import com.graduate.polls.requests.RegisterRequest;
import com.graduate.polls.responses.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}

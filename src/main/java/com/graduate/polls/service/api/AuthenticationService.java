package com.graduate.polls.service.api;

import com.graduate.polls.requests.AuthenticationRequest;
import com.graduate.polls.requests.RegisterRequest;
import com.graduate.polls.responses.AuthenticationResponse;

public interface AuthenticationService {
    /**
     * Register a new user
     * @param request
     * @return
     */
    AuthenticationResponse register(RegisterRequest request);

    /**
     * Authenticate a user
     * @param request
     * @return
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);
}

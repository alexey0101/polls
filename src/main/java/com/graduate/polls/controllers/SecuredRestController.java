package com.graduate.polls.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * This interface is used to mark the controllers that require authentication.
 */
@SecurityRequirement(name = "bearerAuth")
public interface SecuredRestController {
}

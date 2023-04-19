package com.graduate.polls.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotEmpty(message = "Username is required")
  @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters long")
  private String username;
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and no whitespace")
  @NotEmpty(message = "Password is required")
  @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
  private String password;
}

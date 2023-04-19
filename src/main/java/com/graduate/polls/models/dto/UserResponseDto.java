package com.graduate.polls.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

@Data
public class UserResponseDto implements Serializable {
    @Length(min = 1, max = 255, message = "User id cannot be longer than 255 characters and cannot be empty")
    @JsonProperty("user_id")
    private String userId;
    @NotNull
    @Valid
    @Size(min = 1, message = "Answers cannot be empty")
    private List<UserAnswerDto> answers;
}

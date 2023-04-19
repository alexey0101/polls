package com.graduate.polls.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PollDto implements Serializable {

    @NotBlank(message = "Poll name cannot be blank")
    @Size(max = 255, message = "Poll name cannot be longer than 255 characters")
    private String name;
    @NotBlank(message = "Poll description cannot be blank")
    @Size(max = 255, message = "Poll description cannot be longer than 255 characters")
    private String description;
    @NotNull(message = "Poll start question id cannot be null")
    @Positive(message = "Poll start question id must be positive")
    @JsonProperty("start_question_id")
    private Long startQuestionId;
    @JsonProperty("user_id")
    @Size(max = 255, message = "User id cannot be longer than 255 characters")
    private String userId;
    @Valid
    @NotNull(message = "Poll questions cannot be null")
    @Size(min = 1, max = 255, message = "Poll must contain at least one question and no more than 255 questions")
    private List<QuestionDto> questions;
}
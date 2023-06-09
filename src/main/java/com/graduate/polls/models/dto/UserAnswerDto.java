package com.graduate.polls.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserAnswerDto implements Serializable {
    @NotNull(message = "questionId cannot be null")
    @Positive(message = "questionId must be positive")
    @JsonProperty("question_id")
    private Long questionId;
    @Positive(message = "answerId must be positive")
    @JsonProperty("answer_id")
    private Long answerId;
    @JsonProperty("answer_text")
    private String answerText;
    @JsonProperty("scale_value")
    private Long scaleValue;
}

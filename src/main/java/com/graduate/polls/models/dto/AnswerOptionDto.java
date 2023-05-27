package com.graduate.polls.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class AnswerOptionDto implements Serializable {
    @NotBlank(message = "Answer text cannot be blank")
    @Size(max = 255, message = "Answer text cannot be longer than 255 characters")
    @JsonProperty("answer_text")
    private String answerText;
    @Positive(message = "Next question id must be positive")
    @JsonProperty("next_question_id")
    private Long nextQuestionId;
}

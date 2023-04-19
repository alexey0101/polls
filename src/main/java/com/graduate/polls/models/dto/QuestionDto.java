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
public class QuestionDto implements Serializable {
    @NotNull(message = "Question id cannot be null")
    @Positive(message = "Question id must be positive")
    @JsonProperty("question_id")
    private Long questionId;
    @NotBlank(message = "Question text cannot be blank")
    @Size(max = 255, message = "Question text cannot be longer than 255 characters")
    @JsonProperty("question_text")
    private String questionText;
    @NotNull(message = "Question answers cannot be null")
    @Size(min = 1, max = 255, message = "Question must contain at least one answer and no more than 255 answers")
    @Valid
    private List<AnswerOptionDto> answers;
}

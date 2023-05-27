package com.graduate.polls.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graduate.polls.models.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @NotNull(message = "Question type cannot be null")
    @JsonProperty("question_type")
    private QuestionType questionType;
    @Size(min = 1, max = 255, message = "Question must contain at least one answer and no more than 255 answers")
    @Valid
    private List<AnswerOptionDto> answers;
    @Positive(message = "Next question id must be positive")
    @JsonProperty("next_question_id")
    private Long nextQuestionId;
    @Min(value = -1000, message = "Scale min must be greater than or equal to -1000")
    @JsonProperty("scale_min")
    private Long scaleMin;
    @Max(value = 1000, message = "Scale max must be less than or equal to 1000")
    @JsonProperty("scale_max")
    private Long scaleMax;
}

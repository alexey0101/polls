package com.graduate.polls.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionStatistic implements Serializable {
    @JsonProperty("question_id")
    private Long questionId;
    @JsonProperty("total_responses")
    private Long totalResponses;
    private Double percentage;
    @JsonProperty("question_text")
    private String questionText;

    private List<AnswerStatistic> answers;
}

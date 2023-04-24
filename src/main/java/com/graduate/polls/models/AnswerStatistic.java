package com.graduate.polls.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AnswerStatistic implements Serializable {
    @JsonProperty("answer_id")
    private Long answerId;
    @JsonProperty("total_responses")
    private Long totalResponses;
    private Double percentage;
    @JsonProperty("answer_text")
    private String answerText;
}

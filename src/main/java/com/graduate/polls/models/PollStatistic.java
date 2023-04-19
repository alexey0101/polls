package com.graduate.polls.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PollStatistic implements Serializable {
    @JsonProperty("total_responses")
    private Long totalResponses;

    private List<QuestionStatistic> questions;
}

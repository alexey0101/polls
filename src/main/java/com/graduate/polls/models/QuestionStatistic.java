package com.graduate.polls.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
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
    @JsonProperty("question_type")
    private QuestionType questionType;
    @JsonProperty("scale_mean")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal scaleMean;
    @JsonProperty("scale_mode")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long scaleMode;
    @JsonProperty("scale_median")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double scaleMedian;
    @JsonProperty("scale_std")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal scaleStd;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<AnswerStatistic> answers;
}

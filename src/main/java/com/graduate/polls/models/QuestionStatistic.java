package com.graduate.polls.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionStatistic implements Serializable {
    private Long totalResponses;
    private Double percentage;
    private String questionText;

    private List<AnswerStatistic> answers;
}

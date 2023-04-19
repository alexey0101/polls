package com.graduate.polls.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnswerStatistic implements Serializable {
    private Long totalResponses;
    private Double percentage;
    private String answerText;
}

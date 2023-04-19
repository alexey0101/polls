package com.graduate.polls.service.impl;

import com.graduate.polls.models.AnswerStatistic;
import com.graduate.polls.models.PollStatistic;
import com.graduate.polls.models.Question;
import com.graduate.polls.models.QuestionStatistic;
import com.graduate.polls.repository.UserResponseRepository;
import com.graduate.polls.service.api.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final UserResponseRepository userResponseRepository;
    @Override
    public PollStatistic getPollStatistics(Long pollId) {
        PollStatistic pollStatistic = new PollStatistic();
        pollStatistic.setTotalResponses(userResponseRepository.countByPollId(pollId));

        List<QuestionStatistic> questionStatistics = new ArrayList<>();

        for (var question : userResponseRepository.countQuestionResponses(pollId)) {
            QuestionStatistic questionStatistic = new QuestionStatistic();

            questionStatistic.setQuestionText(question.get("question_text", String.class));
            questionStatistic.setTotalResponses(question.get("response_number", Long.class));
            questionStatistic.setPercentage(questionStatistic.getTotalResponses() / (double) pollStatistic.getTotalResponses() * 100);

            List<AnswerStatistic> answerStatistics = new ArrayList<>();

            for (var answer : userResponseRepository.countAnswerResponses(pollId, question.get("id", Long.class))) {
                AnswerStatistic answerStatistic = new AnswerStatistic();

                answerStatistic.setAnswerText(answer.get("answer_text", String.class));
                answerStatistic.setTotalResponses(answer.get("response_number", Long.class));
                answerStatistic.setPercentage(answerStatistic.getTotalResponses() / (double) questionStatistic.getTotalResponses() * 100);

                answerStatistics.add(answerStatistic);
            }

            questionStatistic.setAnswers(answerStatistics);
            questionStatistics.add(questionStatistic);
        }

        pollStatistic.setQuestions(questionStatistics);

        return pollStatistic;
    }
}

package com.graduate.polls.service.api;

import com.graduate.polls.models.Poll;
import com.graduate.polls.models.Question;

import java.util.List;

public interface PollService {
    void createPoll(Poll poll);

    Poll getPoll(Long pollId) throws Exception;

    List<Poll> getPollsByUser(String userId, int page, int size);

    Question getQuestion(Long pollId, Long questionId) throws Exception;
    void deletePoll(Long pollId);

    List<Poll> getAllPolls(int page, int size);
}

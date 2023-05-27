package com.graduate.polls.service.api;

import com.graduate.polls.models.Poll;
import com.graduate.polls.models.Question;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

public interface PollService {
    void createPoll(Poll poll);
    Poll getPoll(Long pollId) throws Exception;
    List<Poll> getPollsByUser(String userId, Pageable pageable);
    Question getQuestion(Long pollId, Long questionId) throws Exception;
    Question getQuestion(Long id) throws Exception;
    List<Question> getQuestions(Long pollId);
    void deletePoll(Long pollId);
    void updatePoll(Poll poll);
    void setActive(Long pollId, boolean active);
    List<Poll> getAllPolls(Pageable pageable, String name, List<String> tags, ZonedDateTime from, ZonedDateTime to);
    List<Poll> getPollsByName(String name, Pageable pageable);
}

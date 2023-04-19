package com.graduate.polls.service.impl;

import com.graduate.polls.models.Poll;
import com.graduate.polls.models.Question;
import com.graduate.polls.repository.PollRepository;
import com.graduate.polls.repository.QuestionRepository;
import com.graduate.polls.repository.UserResponseRepository;
import com.graduate.polls.service.api.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;

    private final QuestionRepository questionRepository;

    private final UserResponseRepository userResponseRepository;

    @Override
    @Transactional
    public void createPoll(Poll poll) {
        pollRepository.save(poll);
    }

    @Override
    public Poll getPoll(Long pollId) throws Exception {
        return pollRepository.findById(pollId).orElseThrow(() -> new Exception("Poll not found"));
    }

    @Override
    public List<Poll> getPollsByUser(String userId, int page, int size) {
        return pollRepository.findAllByUserId(userId, PageRequest.of(page, size)).getContent();
    }

    @Override
    public Question getQuestion(Long pollId, Long questionId) throws Exception {
        return questionRepository.findByIdAndPollId(questionId, pollId).orElseThrow(() -> new Exception("Question not found"));
    }

    @Override
    public void deletePoll(Long pollId) {
        if (pollRepository.findById(pollId).isEmpty()) {
            throw new IllegalArgumentException("There is no poll with such id!");
        }
        pollRepository.deleteById(pollId);
    }

    @Override
    public List<Poll> getAllPolls(int page, int size) {
        return pollRepository.findAll(PageRequest.of(page, size)).getContent();
    }
}

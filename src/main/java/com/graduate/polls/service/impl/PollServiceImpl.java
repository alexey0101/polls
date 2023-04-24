package com.graduate.polls.service.impl;

import com.graduate.polls.models.Poll;
import com.graduate.polls.models.PollStatistic;
import com.graduate.polls.models.Question;
import com.graduate.polls.models.Tag;
import com.graduate.polls.repository.PollRepository;
import com.graduate.polls.repository.QuestionRepository;
import com.graduate.polls.repository.TagRepository;
import com.graduate.polls.repository.UserResponseRepository;
import com.graduate.polls.service.api.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PollServiceImpl implements PollService {
    private final PollRepository pollRepository;

    private final QuestionRepository questionRepository;

    private final UserResponseRepository userResponseRepository;

    private final TagRepository tagRepository;

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
    public List<Poll> getPollsByUser(String userId, Pageable pageable) {
        return pollRepository.findAllByUserId(userId, pageable).getContent();
    }

    @Override
    public Question getQuestion(Long pollId, Long questionId) throws Exception {
        if (pollRepository.findById(pollId).isEmpty()) {
            throw new IllegalArgumentException("There is no poll with such id!");
        }
        return questionRepository.findByIdAndPollId(questionId, pollId).orElseThrow(() -> new Exception("Question not found"));
    }

    @Override
    public List<Question> getQuestions(Long pollId) {
        if (pollRepository.findById(pollId).isEmpty()) {
            throw new IllegalArgumentException("There is no poll with such id!");
        }
        return questionRepository.findAllByPollId(pollId).orElseThrow(() -> new IllegalArgumentException("There is no poll with such id!"));
    }

    @Override
    public void deletePoll(Long pollId) {
        if (pollRepository.findById(pollId).isEmpty()) {
            throw new IllegalArgumentException("There is no poll with such id!");
        }
        pollRepository.deleteById(pollId);
    }

    @Override
    public void updatePoll(Poll poll) {
        if (pollRepository.findById(poll.getId()).isEmpty()) {
            throw new IllegalArgumentException("There is no poll with such id!");
        }
        pollRepository.save(poll);
    }

    @Override
    public void setActive(Long pollId, boolean active) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new IllegalArgumentException("There is no poll with such id!"));
        poll.setActive(active);
        pollRepository.save(poll);
    }

    @Override
    public List<Poll> getAllPolls(Pageable pageable, String name, List<String> tags, ZonedDateTime from, ZonedDateTime to) {
        //throw exception if from or to are way to far in the past or future (e.g. 100 years)
        if (from != null && from.getYear() < 1900) {
            throw new IllegalArgumentException("From date is too far in the past!");
        }
        if (to != null && to.getYear() > 9999) {
            throw new IllegalArgumentException("To date is too far in the future!");
        }

        List<Tag> tagList = new ArrayList<>();

        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                tagList.add(tagRepository.findByName(tag).orElseThrow(() -> new IllegalArgumentException("There is no tag with such name!")));
            }
            return pollRepository.findAll(name.toLowerCase(), tagList, from, to, pageable).getContent();
        }

        return pollRepository.findAll(name.toLowerCase(), from, to, pageable).getContent();
    }

    @Override
    public List<Poll> getPollsByName(String name, Pageable pageable) {
        return pollRepository.findByNameContainingIgnoreCase(name, pageable).getContent();
    }
}

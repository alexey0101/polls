package com.graduate.polls.service.impl;

import com.graduate.polls.models.UserResponse;
import com.graduate.polls.repository.UserResponseRepository;
import com.graduate.polls.service.api.PollService;
import com.graduate.polls.service.api.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private final UserResponseRepository userResponseRepository;

    private final PollService pollService;

    @Override
    public List<UserResponse> getAllPollResponses(Long pollId, LocalDateTime from, LocalDateTime to, Pageable pageable) throws Exception {
        pollService.getPoll(pollId);
        if (from != null && from.getYear() < 1900) {
            throw new IllegalArgumentException("From date is too far in the past!");
        }
        if (to != null && to.getYear() > 9999) {
            throw new IllegalArgumentException("To date is too far in the future!");
        }
        if (from == null) {
            from = LocalDateTime.of(1900, 1, 1, 0, 0);
        }
        if (to == null) {
            to = LocalDateTime.of(9999, 12, 31, 23, 59);
        }
        return userResponseRepository.findAllPollResponses(pollId, from, to, pageable);
    }

    @Override
    public List<UserResponse> getAllPollResponsesByUser(Long pollId, String userId, Pageable pageable) {
        return userResponseRepository.findAllPollResponsesByUser(pollId, userId, pageable);
    }

    @Override
    public List<UserResponse> getAllResponsesByUser(String userId, Pageable pageable) {
        return userResponseRepository.findAllResponsesByUser(userId, pageable);
    }

    @Override
    public UserResponse getResponseById(Long responseId) {
        return userResponseRepository.findUserResponseById(responseId).orElseThrow(() -> new IllegalArgumentException("Response with given id is not found!"));
    }

    @Override
    public void submitPoll(UserResponse userResponse) {
        if (!userResponse.getPoll().isActive()) {
            throw new IllegalArgumentException("Poll is not active!");
        }
        userResponseRepository.save(userResponse);
    }

    @Override
    public void deleteResponseById(Long responseId) {
        if (userResponseRepository.findUserResponseById(responseId).isEmpty()) {
            throw new IllegalArgumentException("There is no response with such id!");
        }
        userResponseRepository.deleteByResponseID(responseId);
    }
}

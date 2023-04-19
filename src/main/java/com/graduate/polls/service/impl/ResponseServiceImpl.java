package com.graduate.polls.service.impl;

import com.graduate.polls.models.UserResponse;
import com.graduate.polls.repository.UserResponseRepository;
import com.graduate.polls.service.api.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private final UserResponseRepository userResponseRepository;

    @Override
    public List<UserResponse> getAllPollResponses(Long pollId, int page, int size) {
        return userResponseRepository.findAllPollResponses(pollId, PageRequest.of(page, size));
    }

    @Override
    public List<UserResponse> getAllPollResponsesByUser(Long pollId, String userId, int page, int size) {
        return userResponseRepository.findAllPollResponsesByUser(pollId, userId, PageRequest.of(page, size));
    }

    @Override
    public UserResponse getResponseById(Long responseId) {
        return userResponseRepository.findUserResponseById(responseId).orElseThrow(() -> new IllegalArgumentException("Response with given id is not found!"));
    }

    @Override
    public void submitPoll(UserResponse userResponse) {
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

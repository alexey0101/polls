package com.graduate.polls.service.api;

import com.graduate.polls.models.UserResponse;

import java.util.List;

public interface ResponseService {
    List<UserResponse> getAllPollResponses(Long pollId, int page, int size);
    List<UserResponse> getAllPollResponsesByUser(Long pollId, String userId, int page, int size);

    List<UserResponse> getAllResponsesByUser(String userId, int page, int size);
    UserResponse getResponseById(Long responseId);
    void submitPoll(UserResponse userResponse);
    void deleteResponseById(Long responseId);
}

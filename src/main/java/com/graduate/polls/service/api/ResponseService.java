package com.graduate.polls.service.api;

import com.graduate.polls.models.UserResponse;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

public interface ResponseService {
    List<UserResponse> getAllPollResponses(Long pollId, ZonedDateTime from, ZonedDateTime to, Pageable pageable) throws Exception;
    List<UserResponse> getAllPollResponsesByUser(Long pollId, String userId, Pageable pageable) throws Exception;

    List<UserResponse> getAllResponsesByUser(String userId, Pageable pageable);
    UserResponse getResponseById(Long responseId);
    void submitPoll(UserResponse userResponse);
    void deleteResponseById(Long responseId);
}

package com.graduate.polls.service.api;

import com.graduate.polls.models.UserResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ResponseService {
    List<UserResponse> getAllPollResponses(Long pollId, LocalDateTime from, LocalDateTime to, Pageable pageable) throws Exception;
    List<UserResponse> getAllPollResponsesByUser(Long pollId, String userId, Pageable pageable);

    List<UserResponse> getAllResponsesByUser(String userId, Pageable pageable);
    UserResponse getResponseById(Long responseId);
    void submitPoll(UserResponse userResponse);
    void deleteResponseById(Long responseId);
}

package com.graduate.polls.controllers;

import com.graduate.polls.models.UserResponse;
import com.graduate.polls.models.dto.UserResponseDto;
import com.graduate.polls.responses.ErrorResponse;
import com.graduate.polls.service.api.ResponseService;
import com.graduate.polls.utils.PollUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserResponseController implements SecuredRestController {

    private final ResponseService responseService;

    private final PollUtil pollUtil;

    /**
     * Get all responses for a poll
     * @param pollId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/polls/{pollId}/responses")
    public ResponseEntity<?> getAllPollResponses(@PathVariable Long pollId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "") LocalDateTime from,
                                                 @RequestParam(defaultValue = "") LocalDateTime to) {
        try {
            Map<String, List<UserResponse>> responses = new HashMap<>();
            responses.put("responses", responseService.getAllPollResponses(pollId, from, to, PageRequest.of(page, size)));
            return ResponseEntity.ok().body(responses);
        } catch (Exception e) {
            if (e.getMessage().equals("Poll not found"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Submit poll response
     * @param id
     * @param userResponse
     * @return
     */
    @PostMapping("/api/v1/polls/{id}/responses")
    public ResponseEntity<?> submitResponse(@PathVariable Long id, @Valid @RequestBody UserResponseDto userResponse) {
        try {
            responseService.submitPoll(pollUtil.convertToEntity(userResponse, id));
            return ResponseEntity.ok(Map.of("message", "Poll submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all responses for a poll by a user
     * @param pollId
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/polls/{pollId}/responses/users/{userId}")
    public ResponseEntity<?> getAllPollResponsesByUser(@PathVariable Long pollId, @PathVariable String userId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, List<UserResponse>> responses = new HashMap<>();
            responses.put("responses", responseService.getAllPollResponsesByUser(pollId, userId, PageRequest.of(page, size)));
            return ResponseEntity.ok().body(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/api/v1/polls/responses/users/{userId}")
    public ResponseEntity<?> getAllResponsesByUser(@PathVariable String userId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, List<UserResponse>> responses = new HashMap<>();
            responses.put("responses", responseService.getAllResponsesByUser(userId, PageRequest.of(page, size)));
            return ResponseEntity.ok().body(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get response by id
     * @param responseId
     * @return
     */
    @GetMapping("/api/v1/polls/responses/{responseId}")
    public ResponseEntity<?> getResponseById(@PathVariable Long responseId) {
        try {
            return ResponseEntity.ok().body(responseService.getResponseById(responseId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete response by id
     * @param responseId
     * @return
     */
    @DeleteMapping("/api/v1/polls/responses/{responseId}")
    public ResponseEntity<?> deleteResponseById(@PathVariable Long responseId) {
        try {
            responseService.deleteResponseById(responseId);
            return ResponseEntity.ok().body(Map.of("message", "Response deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}

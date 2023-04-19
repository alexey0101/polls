package com.graduate.polls.controllers;

import com.graduate.polls.models.UserResponse;
import com.graduate.polls.models.dto.UserResponseDto;
import com.graduate.polls.responses.ErrorResponse;
import com.graduate.polls.service.api.ResponseService;
import com.graduate.polls.utils.PollUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/responses")
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
    @GetMapping("/polls/{pollId}")
    public ResponseEntity<?> getAllPollResponses(@PathVariable Long pollId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, List<UserResponse>> responses = new HashMap<>();
            responses.put("responses", responseService.getAllPollResponses(pollId, page, size));
            return ResponseEntity.ok().body(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Submit poll response
     * @param id
     * @param userResponse
     * @return
     */
    @PostMapping("/polls/{id}")
    public ResponseEntity<?> submitPoll(@PathVariable Long id, @Valid @RequestBody UserResponseDto userResponse) {
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
    @GetMapping("/polls/{pollId}/users/{userId}")
    public ResponseEntity<?> getAllPollResponsesByUser(@PathVariable Long pollId, @PathVariable String userId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, List<UserResponse>> responses = new HashMap<>();
            responses.put("responses", responseService.getAllPollResponsesByUser(pollId, userId, page, size));
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
    @GetMapping("/{responseId}")
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
    @DeleteMapping("/{responseId}")
    public ResponseEntity<?> deleteResponseById(@PathVariable Long responseId) {
        try {
            responseService.deleteResponseById(responseId);
            return ResponseEntity.ok().body(Map.of("message", "Response deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}

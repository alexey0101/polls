package com.graduate.polls.controllers;

import com.graduate.polls.models.dto.PollDto;
import com.graduate.polls.responses.ErrorResponse;
import com.graduate.polls.service.api.PollService;
import com.graduate.polls.service.api.StatisticService;
import com.graduate.polls.utils.PollUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/polls")
@RequiredArgsConstructor
@Validated
public class PollController implements SecuredRestController {

    private final Logger logger = LoggerFactory.getLogger(PollController.class);

    private final PollService pollService;

    private final StatisticService statisticService;

    private final PollUtil pollUtil;

    /**
     * Create new poll from json
     * @param poll
     * @return
     */
    @PostMapping("")
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollDto poll) {
        try {
            pollService.createPoll(pollUtil.convertToEntity(poll));
            return ResponseEntity.ok(Map.of("message", "Poll created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Create new poll from excel file
     * @param file
     * @return
     * @throws MethodArgumentNotValidException
     */
    @PostMapping(value = "/excel")
    public ResponseEntity<?> createPollFromExcel(@RequestParam("file") MultipartFile file) throws MethodArgumentNotValidException {
        try {
            InputStream inputStream = file.getInputStream();
            pollService.createPoll(pollUtil.convertToEntity(WorkbookFactory.create(inputStream)));
            return ResponseEntity.ok(Map.of("message", "Poll created successfully"));
        }
        catch (MethodArgumentNotValidException e) {
            throw new MethodArgumentNotValidException(e.getParameter(), e.getBindingResult());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all polls
     * @param page - page number
     * @param size - page size
     * @return
     */
    @GetMapping("")
    public ResponseEntity<?> getAllPolls(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(Map.of("polls", (pollService.getAllPolls(page, size))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getAllPollsByUserId(@PathVariable String userId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(Map.of("polls", (pollService.getPollsByUser(userId, page, size))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete poll by id
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePoll(@PathVariable Long id) {
        try {
            pollService.deletePoll(id);
            return ResponseEntity.ok(Map.of("message", "Poll deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }


    /**
     * Get poll by id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFullPollData(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pollService.getPoll(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getPollStats(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(statisticService.getPollStatistics(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get question by poll_id and id
     * @param pollId
     * @param questionId
     * @return
     */
    @GetMapping("/{pollId}/questions/{questionId}")
    public ResponseEntity<?> getQuestion(@PathVariable Long pollId, @PathVariable Long questionId) {
        try {
            return ResponseEntity.ok(pollService.getQuestion(pollId, questionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}

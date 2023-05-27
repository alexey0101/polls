package com.graduate.polls.controllers;

import com.graduate.polls.models.dto.PollDto;
import com.graduate.polls.responses.ErrorResponse;
import com.graduate.polls.service.api.PollService;
import com.graduate.polls.service.api.StatisticService;
import com.graduate.polls.utils.PollUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

/**
 * Poll controller
 */
@RestController
@RequestMapping("/api/v1/polls")
@RequiredArgsConstructor
@Validated
public class PollController implements SecuredRestController {
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
     * Create new poll from Excel file
     * @param file
     * @return
     * @throws MethodArgumentNotValidException
     */
    @PostMapping(value = "/excel")
    public ResponseEntity<?> createPollFromExcel(@RequestParam("file") MultipartFile file) throws MethodArgumentNotValidException {
        try {
            if (!file.getOriginalFilename().contains(".xls")) {
                throw new IllegalArgumentException("Excel file is not valid!");
            }
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
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "") String name,
                                         @RequestParam(defaultValue = "2000-01-01T00:00:00") String from,
                                         @RequestParam(defaultValue = "9999-01-01T00:00:00") String to,
                                         @RequestParam(defaultValue = "") List<String> tags) {
        try {
            ZonedDateTime utcDateTimeFrom = LocalDateTime.parse(from, ISO_LOCAL_DATE_TIME).atZone(ZoneOffset.UTC);
            ZonedDateTime utcDateTimeTo = LocalDateTime.parse(to, ISO_LOCAL_DATE_TIME).atZone(ZoneOffset.UTC);
            return ResponseEntity.ok(Map.of("polls", (pollService.getAllPolls(PageRequest.of(page, size), name, tags, utcDateTimeFrom, utcDateTimeTo))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all polls by user id
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getAllPollsByUserId(@PathVariable String userId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(Map.of("polls", (pollService.getPollsByUser(userId, PageRequest.of(page, size)))));
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get poll statistics by id
     * @param id
     * @return
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getPollStats(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(statisticService.getPollStatistics(id));
        } catch (Exception e) {
            if (e.getMessage().equals("Poll not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Set poll active/inactive
     * @param active
     * @param id
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> setActive(@RequestParam boolean active, @PathVariable Long id) {
        try {
            pollService.setActive(id, active);
            return ResponseEntity.ok(Map.of("message", "Poll updated successfully"));
        } catch (Exception e) {
            if (e.getMessage().equals("There is no poll with such id!")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get poll statistics by id in xlsx format
     * @param id
     * @return
     */
    @GetMapping("/{id}/stats/excel")
    public ResponseEntity<?> getPollExcelStats(@PathVariable Long id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "output.xlsx");
            return ResponseEntity.ok().headers(headers).body((statisticService.createPollReport(id)));
        } catch (Exception e) {
            if (e.getMessage().equals("Poll not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            }
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
            if (e.getMessage().equals("There is no poll with such id!") || e.getMessage().equals("Question not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all questions by poll_id
     * @param pollId
     * @return
     */
    @GetMapping("/{pollId}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Long pollId) {
        try {
            return ResponseEntity.ok(Map.of("questions", pollService.getQuestions(pollId)));
        } catch (Exception e) {
            if (e.getMessage().equals("There is no poll with such id!")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }


}

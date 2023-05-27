package com.graduate.polls.controllers;

import com.graduate.polls.models.App;
import com.graduate.polls.models.Tag;
import com.graduate.polls.models.dto.TagDto;
import com.graduate.polls.models.dto.TagsDto;
import com.graduate.polls.responses.ErrorResponse;
import com.graduate.polls.service.api.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Tag controller
 */
@RestController
@RequiredArgsConstructor
public class TagController implements SecuredRestController {
    private final TagService tagService;

    /**
     * Create new tag
     * @param tagDto
     * @return
     */
    @PostMapping("/api/v1/polls/tags")
    public ResponseEntity<?> createTag(@Valid @RequestBody TagDto tagDto) {
        try {
            Tag tag = new Tag(tagDto.getName());
            tag.setApp((App) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            tagService.createTag(tag);
            return ResponseEntity.ok(Map.of("message", "Tag created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all tags
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/polls/tags")
    public ResponseEntity<?> getAllTags(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(Map.of("tags", tagService.getAll(PageRequest.of(page, size))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete tag by id
     * @param id
     * @return
     */
    @DeleteMapping("/api/v1/polls/tags/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.ok(Map.of("message", "Tag deleted successfully"));
        } catch (Exception e) {
            if (e.getMessage().equals("Tag with such id not found!"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Add tags to poll
     * @param pollId
     * @param tagsDto
     * @return
     */
    @PostMapping("/api/v1/polls/{pollId}/tags")
    public ResponseEntity<?> addTagsToPoll(@PathVariable Long pollId, @Valid @RequestBody TagsDto tagsDto) {
        try {
            tagService.addTagsToPoll(pollId, tagsDto.getTags());
            return ResponseEntity.ok(Map.of("message", "Tags added successfully"));
        } catch (Exception e) {
            if (e.getMessage().equals("Poll not found"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all tags by poll id
     * @param pollId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/api/v1/polls/{pollId}/tags")
    public ResponseEntity<?> getPollTags(@PathVariable Long pollId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(Map.of("tags", tagService.getAllByPollId(pollId, PageRequest.of(page, size))));
        } catch (Exception e) {
            if (e.getMessage().equals("Poll not found"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Remove tag from poll
     * @param pollId
     * @param tagId
     * @return
     */
    @DeleteMapping("/api/v1/polls/{pollId}/tags/{tagId}")
    public ResponseEntity<?> removeTagFromPoll(@PathVariable Long pollId, @PathVariable Long tagId) {
        try {
            tagService.removeTagFromPoll(pollId, tagId);
            return ResponseEntity.ok(Map.of("message", "Tag removed successfully"));
        } catch (Exception e) {
            if (e.getMessage().equals("Poll not found") || e.getMessage().equals("Tag not found"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}

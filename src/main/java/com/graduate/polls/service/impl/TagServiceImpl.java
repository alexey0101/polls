package com.graduate.polls.service.impl;

import com.graduate.polls.models.Poll;
import com.graduate.polls.models.Tag;
import com.graduate.polls.repository.TagRepository;
import com.graduate.polls.service.api.PollService;
import com.graduate.polls.service.api.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    private final PollService pollService;

    @Override
    public Tag getByName(String name) {
        return tagRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("Tag with such name not found!"));
    }

    @Override
    public Tag getById(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tag with such id not found!"));
    }

    @Override
    public List<Tag> getAllByPollId(Long pollId, Pageable pageable) throws Exception {
        pollService.getPoll(pollId);
        return tagRepository.findAllByPollId(pollId, pageable).getContent();
    }

    @Override
    public List<Tag> getAll(Pageable pageable) {
        return tagRepository.findAll(pageable).getContent();
    }

    @Override
    public void createTag(Tag tag) {
        if (tagRepository.findByName(tag.getName()).isPresent()) {
            throw new IllegalArgumentException("Tag with such name already exists!");
        }
        tag.setName(tag.getName().toLowerCase());
        tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Long id) {
        if (tagRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Tag with such id not found!");
        }
        tagRepository.deleteById(id);
    }

    @Override
    public void addTagsToPoll(Long pollId, List<Long> tagIds) throws Exception {
        Poll poll = pollService.getPoll(pollId);
        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new Exception("Tag %d not found".formatted(tagId)));
            if (!poll.getTags().contains(tag)) {
                poll.getTags().add(tag);
            } else {
                throw new IllegalArgumentException("Tag %d already exists in poll %d".formatted(tagId, pollId));
            }
        }
        pollService.updatePoll(poll);
    }

    @Override
    public void removeTagFromPoll(Long pollId, Long tagId) throws Exception {
        Poll poll = pollService.getPoll(pollId);
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new IllegalArgumentException("Tag not found".formatted(tagId)));
        poll.getTags().remove(tag);
        pollService.updatePoll(poll);
    }
}

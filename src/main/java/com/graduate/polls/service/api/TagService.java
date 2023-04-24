package com.graduate.polls.service.api;

import com.graduate.polls.models.Tag;
import org.springframework.data.domain.Pageable;

import javax.swing.text.TabableView;
import java.util.List;

public interface TagService {
    Tag getByName(String name);
    Tag getById(Long id);
    List<Tag> getAllByPollId(Long pollId, Pageable pageable) throws Exception;
    List<Tag> getAll(Pageable pageable);
    void createTag(Tag tag);
    void deleteTag(Long id);
    void addTagsToPoll(Long pollId, List<Long> tagIds) throws Exception;
    void removeTagFromPoll(Long pollId, Long tagId) throws Exception;
}

package com.graduate.polls.repository;

import com.graduate.polls.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.questionId = ?1 AND q.poll.id = ?2 AND q.poll.app.id = ?#{principal?.id }")
    public Optional<Question> findByIdAndPollId(Long questionId, Long pollId);
}

package com.graduate.polls.repository;

import com.graduate.polls.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.questionId = ?1 AND q.poll.id = ?2 AND q.poll.app.id = ?#{principal?.id }")
    Optional<Question> findByQuestionIdIdAndPollId(Long questionId, Long pollId);


    @Query("SELECT q FROM Question q WHERE q.id = ?1 AND q.poll.app.id = ?#{principal?.id }")
    Optional<Question> findById(Long id);

    @Query("SELECT p.questions FROM Poll p WHERE p.id = ?1 AND p.app.id = ?#{principal?.id }")
    Optional<List<Question>> findAllByPollId(Long pollId);
}

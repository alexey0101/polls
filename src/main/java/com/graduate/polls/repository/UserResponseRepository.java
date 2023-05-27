package com.graduate.polls.repository;

import com.graduate.polls.models.UserResponse;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
    @Query("SELECT r FROM UserResponse r JOIN r.poll p WHERE p.id = :pollId AND p.createdAt >= :from AND p.createdAt <= :to AND r.app.id = ?#{principal?.id }")
    List<UserResponse> findAllPollResponses(@Param("pollId") Long pollId, @Param("from") ZonedDateTime from, @Param("to") ZonedDateTime to, Pageable pageable);

    @Query("SELECT r FROM UserResponse r WHERE r.poll.id = ?1 AND r.userId = ?2 AND r.poll.app.id= ?#{principal?.id }")
    List<UserResponse> findAllPollResponsesByUser(Long pollId, String userId, Pageable pageable);

    @Query("SELECT r FROM UserResponse r WHERE r.userId = ?1 AND r.poll.app.id = ?#{principal?.id }")
    List<UserResponse> findAllResponsesByUser(String userId, Pageable pageable);

    @Query("SELECT r FROM UserResponse r WHERE r.id = ?1 AND r.poll.app.id = ?#{principal?.id }")
    Optional<UserResponse> findUserResponseById(Long responseId);

    @Query("DELETE FROM UserResponse r WHERE r.app.id = ?#{principal?.id } AND r.id = ?1")
    @Modifying
    @Transactional
    void deleteByResponseID(Long responseId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM user_response WHERE app_id = ?#{principal?.id } AND poll_id = ?1")
    Long countByPollId(Long pollId);

    @Query(nativeQuery = true, value = "SELECT user_answer.question_id as question_id, COUNT(*) as response_number," +
            " AVG(user_answer.scale_value) as scale_mean, STDDEV(user_answer.scale_value) as scale_std," +
            " MODE() WITHIN GROUP (ORDER BY scale_value) as scale_mode, " +
            " PERCENTILE_CONT(0.5) WITHIN GROUP(ORDER BY scale_value) as scale_median FROM user_response " +
            " JOIN user_answer ON user_response.id = user_answer.response_id WHERE user_response.app_id = 1" +
            " AND user_response.poll_id = ?1 GROUP BY user_answer.question_id ORDER BY user_answer.question_id ASC")
    List<Tuple> calcAnswerStatistic(Long pollId);

    @Query(nativeQuery = true, value = "SELECT answer_option.id as id, answer_option.answer_text as answer_text, COUNT(*) as response_number" +
            " FROM user_response JOIN user_answer ON user_response.id = user_answer.response_id JOIN answer_option ON user_answer.answer_option_id = answer_option.id " +
            "WHERE user_response.app_id = ?#{principal?.id } AND user_response.poll_id = ?1 AND user_answer.question_id = ?2 GROUP BY answer_option.id")
    List<Tuple> calcAnswerOptionStatistic(Long pollId, Long questionId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM (SELECT COUNT(*) FROM user_answer JOIN user_response on user_response.id = user_answer.response_id" +
            " WHERE user_response.poll_id = ?1 AND user_answer.question_id = ?2 AND user_response.app_id = 1 GROUP BY user_answer.response_id) t")
    Long countMultipleChoiceResponsesByResponseIdAndQuestionId(Long pollId, Long questionId);
}
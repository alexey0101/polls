package com.graduate.polls.repository;

import com.graduate.polls.models.UserResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
    @Query("SELECT r FROM UserResponse r WHERE r.poll.id = ?1 AND r.poll.app.id = ?#{principal?.id }")
    public List<UserResponse> findAllPollResponses(Long pollId, Pageable pageable);

    @Query("SELECT r FROM UserResponse r WHERE r.poll.id = ?1 AND r.userId = ?2 AND r.poll.app.id= ?#{principal?.id }")
    public List<UserResponse> findAllPollResponsesByUser(Long pollId, String userId, Pageable pageable);

    @Query("SELECT r FROM UserResponse r WHERE r.id = ?1 AND r.poll.app.id = ?#{principal?.id }")
    public Optional<UserResponse> findUserResponseById(Long responseId);

    @Query("DELETE FROM UserResponse r WHERE r.app.id = ?#{principal?.id } AND r.id = ?1")
    @Modifying
    @Transactional
    public void deleteByResponseID(Long responseId);
}

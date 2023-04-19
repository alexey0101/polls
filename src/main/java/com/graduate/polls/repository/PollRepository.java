package com.graduate.polls.repository;

import com.graduate.polls.models.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {

    @Query(value = "SELECT p FROM Poll p WHERE p.app.id = ?#{principal?.id }")
    public Page<Poll> findAll(Pageable pageable);

    @Query(value = "SELECT p FROM Poll p WHERE p.userId = ?1 AND p.app.id = ?#{principal?.id }")
    public Page<Poll> findAllByUserId(String userId, Pageable pageable);

    @Query("DELETE FROM Poll p WHERE p.id = ?1 AND p.app.id = ?#{principal?.id}")
    @Modifying
    @Transactional
    public void deleteById(Long pollId);

    @Query("SELECT p FROM Poll p WHERE p.id = ?1 AND p.app.id = ?#{principal?.id}")
    public Optional<Poll> findById(Long pollId);

}

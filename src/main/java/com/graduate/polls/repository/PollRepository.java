package com.graduate.polls.repository;

import com.graduate.polls.models.Poll;
import com.graduate.polls.models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {

    @Query(value = "SELECT p FROM Poll p JOIN p.tags t WHERE t IN :tags AND LOWER(p.name) LIKE CONCAT('%', :name, '%') AND p.createdAt >= :fromDate AND p.createdAt <= :toDate AND p.app.id = ?#{principal?.id }")
    Page<Poll> findAll(@Param("name") String name, @Param("tags") List<Tag> tags, @Param("fromDate") ZonedDateTime from, @Param("toDate") ZonedDateTime to, Pageable pageable);
    @Query(value = "SELECT p FROM Poll p WHERE LOWER(p.name) LIKE CONCAT('%', :name, '%') AND p.createdAt >= :fromDate AND p.createdAt <= :toDate AND p.app.id = ?#{principal?.id }")
    Page<Poll> findAll(@Param("name") String name, @Param("fromDate") ZonedDateTime from, @Param("toDate") ZonedDateTime to, Pageable pageable);

    @Query(value = "SELECT p FROM Poll p WHERE LOWER(p.name) LIKE CONCAT('%', ?1, '%') AND p.app.id = ?#{principal?.id }")
    Page<Poll> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query(value = "SELECT p FROM Poll p WHERE p.userId = ?1 AND p.app.id = ?#{principal?.id }")
    Page<Poll> findAllByUserId(String userId, Pageable pageable);

    @Query("SELECT p FROM Poll p JOIN p.tags t WHERE t IN ?1 AND p.app.id = ?#{principal?.id }")
    Page<Poll> findAllByTags(List<Tag> tags, Pageable pageable);

    @Query("DELETE FROM Poll p WHERE p.id = ?1 AND p.app.id = ?#{principal?.id}")
    @Modifying
    @Transactional
    void deleteById(Long pollId);

    @Query("SELECT p FROM Poll p WHERE p.id = ?1 AND p.app.id = ?#{principal?.id}")
    Optional<Poll> findById(Long pollId);
}

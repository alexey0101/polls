package com.graduate.polls.repository;

import com.graduate.polls.models.Tag;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t FROM Tag t WHERE t.id = ?1 AND t.app.id = ?#{principal?.id }")
    Optional<Tag> findById(Long id);

    @Query("SELECT p.tags FROM Poll p WHERE p.id = ?1 AND p.app.id = ?#{principal?.id }")
    Page<Tag> findAllByPollId(Long pollId, Pageable pageable);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) = LOWER(?1) AND t.app.id = ?#{principal?.id }")
    Optional<Tag> findByName(String name);

    @Query("SELECT t FROM Tag t WHERE t.app.id = ?#{principal?.id }")
    Page<Tag> findAll(Pageable pageable);

    @Query("DELETE FROM Tag t WHERE t.app.id = ?#{principal?.id } AND t.id = ?1")
    @Modifying
    @Transactional
    void deleteById(Long id);
}

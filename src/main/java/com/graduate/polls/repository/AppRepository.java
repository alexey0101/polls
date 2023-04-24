package com.graduate.polls.repository;

import com.graduate.polls.models.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {
    @Query("SELECT a FROM App a WHERE a.username = ?1")
    Optional<App> findByUsername(String username);
}

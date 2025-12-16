package com.fullthrottle.repositories;

import com.fullthrottle.models.Tag;
import com.fullthrottle.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
    Optional<Tag> findById(Long id);

    @Query("SELECT DISTINCT t FROM Tag t " +
                "JOIN t.tasks task " +
                "WHERE task.creator = :user")
    List<Tag> findTagsByUser(@Param("user") User user);

}
package com.fullthrottle.repositories;

import com.fullthrottle.models.Priority;
import com.fullthrottle.models.Task;
import com.fullthrottle.models.TaskStatus;
import com.fullthrottle.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCreatorId(Long creatorId);
    List<Task> findByCreator(User creator);

    @Query("SELECT DISTINCT t FROM Task t " +
            "LEFT JOIN t.taskTags tt " +
            "LEFT JOIN tt.tag tag " +
            "WHERE (:tag IS NULL OR tag.name = :tag) " +
            "AND (:status IS NULL OR t.status = :status)")
    Page<Task> findFilteredTasks(
            @Param("tag") String tag,
            @Param("status") TaskStatus status,
            Pageable pageable);

    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.tags tag " +
            "WHERE LOWER(tag.name) = LOWER(:tag)")
    List<Task> findByTagName(@Param("tag") String tag);

    Page<Task> findByCreator(User creator, Pageable pageable);

    @Query("SELECT t FROM Task t " +
            "WHERE t.creator = :user " +
            "AND (:tag IS NULL OR EXISTS (SELECT tt FROM t.taskTags tt WHERE tt.tag.name LIKE %:tag%)) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:priority IS NULL OR t.priority = :priority)")
    Page<Task> findFilteredTasksForUser(
            @Param("user") User user,
            @Param("tag") String tag,
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            Pageable pageable);

}
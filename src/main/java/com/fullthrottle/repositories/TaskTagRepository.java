package com.fullthrottle.repositories;

import com.fullthrottle.models.TaskTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TaskTagRepository extends JpaRepository<TaskTag, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM TaskTag tt WHERE tt.task.id = :taskId")
    void deleteByTaskId(Long taskId);
}
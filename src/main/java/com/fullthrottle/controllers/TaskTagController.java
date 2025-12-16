package com.fullthrottle.controllers;

import com.fullthrottle.models.TaskTag;
import com.fullthrottle.services.TaskTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-tags")
public class TaskTagController {

    @Autowired
    private TaskTagService taskTagService;

    // Создать связь между задачей и тегом
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public TaskTag createTaskTag(@RequestBody TaskTag taskTag) {
        return taskTagService.createTaskTag(taskTag);
    }

    // Получить связь по ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskTag> getTaskTagById(@PathVariable Long id) {
        return taskTagService.getTaskTagById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Получить все связи
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskTag> getAllTaskTags() {
        return taskTagService.getAllTaskTags();
    }

    // Обновить связь
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskTag> updateTaskTag(@PathVariable Long id, @RequestBody TaskTag taskTagDetails) {
        TaskTag updatedTaskTag = taskTagService.updateTaskTag(id, taskTagDetails);
        return ResponseEntity.ok(updatedTaskTag);
    }

    // Удалить связь
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTaskTag(@PathVariable Long id) {
        taskTagService.deleteTaskTag(id);
        return ResponseEntity.noContent().build();
    }
}
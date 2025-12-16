package com.fullthrottle.controllers;

import com.fullthrottle.models.Task;
import com.fullthrottle.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Создать задачу
    @PostMapping
    @PreAuthorize("isAuthenticated()")

    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    // Получить задачу по id
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Получить задачи по id создателя
    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Task> getTasksByCreatorId(@PathVariable Long creatorId) {
        return taskService.getTasksByCreatorId(creatorId);
    }

    // Обновить задачу
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Task updatedTask = taskService.updateTask(id, taskDetails);
        return ResponseEntity.ok(updatedTask);
    }

    // Удалить задачу
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // Получить все задачи
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/user/{email}/tag/{tagId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getTasksByUserEmailAndTagId(
            @PathVariable String email,
            @PathVariable Long tagId) {
        List<Task> tasks = taskService.getTasksByUserEmailAndTagId(email, tagId);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/user/{email}/tag/{tagName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTasksByUserEmailAndTagName(
            @PathVariable String email,
            @PathVariable String tagName) {
        taskService.deleteTasksByUserEmailAndTagName(email, tagName);
        return ResponseEntity.ok("Задачи пользователя с email " + email + " и тегом '" + tagName + "' удалены.");
    }
}
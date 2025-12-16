package com.fullthrottle.services;

import com.fullthrottle.models.*;
import com.fullthrottle.repositories.TagRepository;
import com.fullthrottle.repositories.TaskRepository;
import com.fullthrottle.repositories.TaskTagRepository;
import com.fullthrottle.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TaskTagRepository taskTagRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public Page<Task> getFilteredTasks(String tag, String status, Pageable pageable) {
        TaskStatus taskStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                taskStatus = TaskStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Если статус невалидный, просто игнорируем его
            }
        }
        
        return taskRepository.findFilteredTasks(
            tag,
            taskStatus,
            pageable
        );
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByTag(String tag) {
        return taskRepository.findByTagName(tag);
    }

    @Transactional(readOnly = true)
    public Page<Task> getTasksByCreator(User creator, Pageable pageable) {
        return taskRepository.findByCreator(creator, pageable);
    }
    
    // Создать задачу
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    // Получить задачу по id
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Получить задачи по id создателя
    public List<Task> getTasksByCreatorId(Long creatorId) {
        return taskRepository.findByCreatorId(creatorId);
    }

    // Удалить задачу по id
    @Transactional  // Указываем, что метод должен выполняться в транзакции
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));  // или кастомное исключение
        taskTagRepository.deleteByTaskId(taskId);  // Удаляем связи с тегами
        taskRepository.delete(task);  // Удаляем саму задачу
    }

    public List<Task> getTasksByUserEmailAndTag(String email, String tagName) {
        // Находим пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Находим тег по имени
        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + tagName));

        List<Task> userTasks = taskRepository.findByCreator(user);
        //List<Task> userTasks = taskRepository.findByCreator(user);
       

        return userTasks.stream()
                .filter(task -> task.getTaskTags().stream()
                        .anyMatch(taskTag -> taskTag.getTag().equals(tag)))
                .collect(Collectors.toList());
    }

    // Обновить задачу
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        return taskRepository.save(task);
    }

    // Получить все задачи
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public List<Task> getTasksByUserEmailAndTagId(String email, Long tagId) {
        // Находим пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Находим тег по ID
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + tagId));

        // Получаем все задачи пользователя
        List<Task> userTasks = taskRepository.findByCreator(user);

        // Фильтруем задачи по тегу
        return userTasks.stream()
                .filter(task -> task.getTaskTags().stream()
                        .anyMatch(taskTag -> taskTag.getTag().equals(tag)))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTasksByUserEmailAndTagName(String email, String tagName) {
        // Находим пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Находим тег по названию
        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + tagName));

        // Получаем все задачи пользователя
        List<Task> userTasks = taskRepository.findByCreator(user);

        // Удаляем задачи, связанные с указанным тегом
        userTasks.forEach(task -> {
            if (task.getTaskTags().stream().anyMatch(taskTag -> taskTag.getTag().equals(tag))) {
                // Удаляем связи между задачей и тегами
                taskTagRepository.deleteByTaskId(task.getId());
                // Удаляем саму задачу
                taskRepository.delete(task);
            }
        });
    }

    public Task createTaskWithTags(Task task, String tagsInput) {
        Task taskToSave = task; // или создайте новый экземпляр, если нужно

        if (tagsInput != null && !tagsInput.trim().isEmpty()) {
            String[] tagNames = tagsInput.split(",");
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (!tagName.isEmpty()) {
                    String currentTagName = tagName; // effectively final копия
                    Tag tag = tagRepository.findByName(currentTagName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(currentTagName);
                                return tagRepository.save(newTag);
                            });
                    taskToSave.addTag(tag);
                }
            }
        }

        return taskRepository.save(taskToSave);
    }

    @Transactional(readOnly = true)
    public Page<Task> getFilteredTasksForUser(
            User user,
            String tag,
            TaskStatus status,
            Priority priority,
            Pageable pageable) {

        // Реализация метода в репозитории
        return taskRepository.findFilteredTasksForUser(
                user,
                tag,
                status,
                priority,
                pageable
        );
    }

    @Transactional
    public Task updateTaskWithTags(Long taskId, Long userId, Task taskDetails, String tagsInput) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getCreator().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        // Обновляем основные поля
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());

        // Обновляем теги, если они указаны
        if (tagsInput != null && !tagsInput.trim().isEmpty()) {
            task.getTags().clear();

            String[] tagNames = tagsInput.split(",");
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (!tagName.isEmpty()) {
                    String finalTagName = tagName;  // Создаем effectively final копию
                    Tag tag = tagRepository.findByName(finalTagName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(finalTagName);  // Теперь корректно
                                return tagRepository.save(newTag);
                            });
                    task.addTag(tag);
                }
            }
        }

        return taskRepository.save(task);
    }

}
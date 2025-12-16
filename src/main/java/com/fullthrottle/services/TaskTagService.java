package com.fullthrottle.services;

import com.fullthrottle.models.TaskTag;
import com.fullthrottle.repositories.TaskTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskTagService {

    @Autowired
    private TaskTagRepository taskTagRepository;

    // Получить связь по ID
    public Optional<TaskTag> getTaskTagById(Long id) {
    return taskTagRepository.findById(id);
    }

    // Создать связь между задачей и тегом
    public TaskTag createTaskTag(TaskTag taskTag) {
        return taskTagRepository.save(taskTag);
    }

    // Получить все связи
    public List<TaskTag> getAllTaskTags() {
        return taskTagRepository.findAll();
    }

    // Обновить связь
    public TaskTag updateTaskTag(Long id, TaskTag taskTagDetails) {
        TaskTag taskTag = taskTagRepository.findById(id).orElseThrow(() -> new RuntimeException("TaskTag not found"));
        taskTag.setTask(taskTagDetails.getTask());
        taskTag.setTag(taskTagDetails.getTag());
        return taskTagRepository.save(taskTag);
    }
    
    // Удалить связь по id
    public void deleteTaskTag(Long id) {
        taskTagRepository.deleteById(id);
    }
}
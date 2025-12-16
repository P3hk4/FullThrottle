package com.fullthrottle.controllers.views;

import com.fullthrottle.DTO.CreateTaskRequest;
import com.fullthrottle.models.Priority;
import com.fullthrottle.models.Tag;
import com.fullthrottle.models.Task;
import com.fullthrottle.models.TaskStatus;
import com.fullthrottle.security.UserDetailsImpl;
import com.fullthrottle.services.TagService;
import com.fullthrottle.services.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService taskService;
    private final TagService tagService;

    public TaskViewController(TaskService taskService, TagService tagService) {
        this.taskService = taskService;
        this.tagService = tagService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String getAllTasks(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // Получаем задачи с учетом фильтров
        Page<Task> tasks;

        // Если все фильтры пустые (значения по умолчанию)
        if ((tag == null || tag.isEmpty()) &&
                (status == null || status.isEmpty()) &&
                (priority == null || priority.isEmpty())) {

            // Просто получаем все задачи пользователя
            tasks = taskService.getTasksByCreator(
                    userDetails.getUser(),
                    PageRequest.of(page - 1, size)
            );
        } else {
            // Применяем фильтры
            TaskStatus taskStatus = null;
            if (status != null && !status.isEmpty()) { //проверяем, что фильтр статуса не пустой
                try {
                    taskStatus = TaskStatus.valueOf(status.toUpperCase()); //преобразование в enum + верхний регистр
                } catch (IllegalArgumentException e) {
                    // Если статус невалидный, игнорируем (null)
                }
            }

            Priority taskPriority = null;
            if (priority != null && !priority.isEmpty()) {
                try {
                    taskPriority = Priority.valueOf(priority.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Если приоритет невалидный, игнорируем
                }
            }

            tasks = taskService.getFilteredTasksForUser(
                    userDetails.getUser(),
                    tag,
                    taskStatus,
                    taskPriority,
                    PageRequest.of(page - 1, size)
            );
        }

        // Получаем теги текущего пользователя
        List<Tag> userTags = tagService.getAllTagsByCreator(userDetails.getUser());

        // Передаем параметры в модель
        model.addAttribute("allTags", userTags);
        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tasks.getTotalPages());
        model.addAttribute("selectedTag", tag); // Для сохранения выбранного тега в форме
        model.addAttribute("selectedStatus", status); // Для сохранения выбранного статуса
        model.addAttribute("selectedPriority", priority); // Для сохранения выбранного приоритета

        if (tasks.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, tasks.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "tasks/allTasks";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "tasks/createTask";
    }

    @PostMapping("/create")
    public String createTask(
            @ModelAttribute CreateTaskRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttrs) {

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setCreator(userDetails.getUser());

        taskService.createTaskWithTags(task, request.getTagsInput());
        redirectAttrs.addFlashAttribute("success", "Задача успешно создана");
        return "redirect:/tasks";
    }

    @GetMapping("/filter")
    public String getTasksByTag(
            @RequestParam String tag,
            Model model) {
        
        List<Task> tasks = taskService.getTasksByTag(tag);
        model.addAttribute("tasks", tasks);
        model.addAttribute("tagName", tag);
        return "tasks/filteredTasks";
    }

    @RequestMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttrs) {

        // Проверяем, что задача принадлежит текущему пользователю
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent() && task.get().getCreator().getId().equals(userDetails.getUser().getId())) {
            taskService.deleteTask(id);
            redirectAttrs.addFlashAttribute("success", "Задача успешно удалена");
        } else {
            redirectAttrs.addFlashAttribute("error", "Не удалось удалить задачу");
        }

        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {

        Optional<Task> task = taskService.getTaskById(id);

        if (task.isEmpty() || !task.get().getCreator().getId().equals(userDetails.getUser().getId())) {
            return "redirect:/tasks";
        }

        model.addAttribute("task", task.get());
        return "tasks/editTask";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateTask(
            @PathVariable Long id,
            @ModelAttribute Task taskDetails,
            @RequestParam(required = false) String tagsInput,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttrs) {

        try {
            Task updatedTask = taskService.updateTaskWithTags(id, userDetails.getUser().getId(), taskDetails, tagsInput);
            redirectAttrs.addFlashAttribute("success", "Задача успешно обновлена");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", "Не удалось обновить задачу: " + e.getMessage());
        }

        return "redirect:/tasks";
    }

}
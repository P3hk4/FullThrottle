package com.fullthrottle.DTO;

import java.time.LocalDateTime;

public class CreateTaskRequest {
    private String title;
    private String description;
    private String tagsInput;
    private LocalDateTime dueDate;

    public CreateTaskRequest() {
    }

    public CreateTaskRequest(String title, String description, String tagsInput, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.tagsInput = tagsInput;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTagsInput() {
        return tagsInput;
    }

    public void setTagsInput(String tagsInput) {
        this.tagsInput = tagsInput;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
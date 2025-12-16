package com.fullthrottle.controllers;

import com.fullthrottle.models.Tag;
import com.fullthrottle.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    // Создать тег
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Tag createTag(@RequestBody Tag tag) {
        return tagService.createTag(tag);
    }

    // Обновить тег
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tagDetails) {
        Tag updatedTag = tagService.updateTag(id, tagDetails);
        return ResponseEntity.ok(updatedTag);
    }

    // Получить тег по ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
        return tagService.getTagById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Удалить тег
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        
        return ResponseEntity.noContent().build();
    }

    // Получить все теги
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }
}
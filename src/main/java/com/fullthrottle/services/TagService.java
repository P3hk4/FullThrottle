package com.fullthrottle.services;

import com.fullthrottle.models.Tag;
import com.fullthrottle.models.User;
import com.fullthrottle.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    // Создать тег
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    // Получить тег по id
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    // Удалить тег по id
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    // Обновить тег
    public Tag updateTag(Long id, Tag tagDetails) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new RuntimeException("Tag not found"));
        tag.setName(tagDetails.getName());
        return tagRepository.save(tag);
    }

    // Получить все теги
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public List<Tag> getAllTagsByCreator(User user) {
        return tagRepository.findTagsByUser(user);
    }

}
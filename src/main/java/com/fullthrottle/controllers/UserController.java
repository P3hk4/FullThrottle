package com.fullthrottle.controllers;

import com.fullthrottle.models.User;
import com.fullthrottle.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Создать пользователя
    @PostMapping
    @PreAuthorize("permitAll()")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // Получить пользователя по ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Получить пользователя по email
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

     // Удалить пользователя
     @DeleteMapping("/{id}")
     @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
         userService.deleteUser(id);
         return ResponseEntity.noContent().build();
     }

    // Удалить пользователя по email
    @DeleteMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserByEmail(@PathVariable String email) {
        userService.deleteUserByEmail(email);
    }

    // Получить всех пользователей
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
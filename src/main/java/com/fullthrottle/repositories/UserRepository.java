package com.fullthrottle.repositories;

import com.fullthrottle.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Найти пользователя по email
    Optional<User> findByEmail(String email);

    // Проверить, существует ли пользователь с указанным email
    boolean existsByEmail(String email);

    // Удалить пользователя по email
    void deleteByEmail(String email);
}
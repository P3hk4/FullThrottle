package com.fullthrottle.controllers.views;

import com.fullthrottle.DTO.AuthRequest;
import com.fullthrottle.models.User;
import com.fullthrottle.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthViewController {

    private final UserService userService;

    public AuthViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) { 
        model.addAttribute("authRequest", new AuthRequest()); // Добавление в модель нового объекта AuthRequest для формы входа
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute AuthRequest authRequest, // Получение данных формы, автоматически связываемых с AuthRequest
            RedirectAttributes redirectAttrs // Объект для передачи данных при редиректе
    ) {
        try {
            // Создаем пользователя через UserService
            User user = new User();
            user.setEmail(authRequest.getEmail());
            user.setPassword(authRequest.getPassword()); // Пароль будет закодирован в сервисе
            user.setName(authRequest.getName());
            userService.createUser(user);
            
            redirectAttrs.addFlashAttribute(
                "success", 
                "Регистрация успешна! Теперь вы можете войти."
            );
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute(
                "error", 
                "Ошибка регистрации: " + e.getMessage()
            );
            return "redirect:/auth/register";
        }
    }
}
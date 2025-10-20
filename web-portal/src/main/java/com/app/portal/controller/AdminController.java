package com.app.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.app.portal.service.AuthClient;
import com.app.portal.service.AuthClient.CreateUserForm;
import com.app.portal.dto.UserDto;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private AuthClient auth;

    public AdminController(AuthClient auth) {
        this.auth = auth;
    }

    @GetMapping("/admin/users")
    public String listarUsuarios(Model model, HttpSession session) {
        List<Map<String, Object>> rawUsers = auth.listUsers();

        List<UserDto> usuarios = rawUsers.stream().map(map -> {
            UserDto dto = new UserDto();
            Object idObj = map.get("id");
            if (idObj != null) {
                try {
                    dto.setId(UUID.fromString(idObj.toString()));
                } catch (IllegalArgumentException e) {
                    dto.setId(null);
                }
            }
            dto.setEmail((String) map.get("email"));
            dto.setName((String) map.get("name"));
            dto.setRole((String) map.get("role"));
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("user", session.getAttribute("user"));
        return "/admin/users";
    }

    @GetMapping("/admin/crear")
    public String showCreateUserForm(Model model) {
        model.addAttribute("form", new CreateUserForm("", "", "", ""));
        return "admin/users-new"; // tu template HTML Thymeleaf
    }

    @PostMapping("/admin/crear")
    public String createUser(
            @ModelAttribute("form") CreateUserForm form,
            HttpSession session,
            Model model
    ) {
        var err = new StringBuilder();
        boolean ok = auth.createUser(form, err);

        if (!ok) {
            model.addAttribute("error", err.toString());
            model.addAttribute("user", session.getAttribute("user"));
            return "admin/users-new";
        }

        return "redirect:/admin/users";
    }

    @PostMapping("admin/eliminar")
    public String deleteUser(@RequestParam String userId, Model model) {
        var err = new StringBuilder();
        var ok = auth.deleteUser(userId, err);

        if (!ok) {
            model.addAttribute("error", err.toString());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("admin/rol")
    public String changeRole(@RequestParam String userId,
            @RequestParam String role,
            Model model) {
        var err = new StringBuilder();
        var ok = auth.changeUserRole(userId, role, err);

        if (!ok) {
            model.addAttribute("error", err.toString());
        }

        return "redirect:/admin/users";
    }
}

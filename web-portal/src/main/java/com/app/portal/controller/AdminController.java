package com.app.portal.controller;

import com.app.portal.service.AuthClient;
import com.app.portal.session.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controlador de administración de usuarios
 */
@Controller
@RequestMapping("/admin/users")
public class AdminController {

    private final AuthClient auth;
    private final CurrentUser current;

    public static class CreateUserForm {
        private String email;
        private String name;
        private String role;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public AdminController(AuthClient auth, CurrentUser current) {
        this.auth = auth;
        this.current = current;
    }

    // LISTA DE USUARIOS
    @GetMapping
    public String users(Model model) {
        var users = auth.listUsers();
        model.addAttribute("users", users);
        model.addAttribute("user", current); // importante para navbar
        return "admin/users";
    }

    // FORMULARIO NUEVO USUARIO
    @GetMapping("/new")
    public String newUser(Model model) {
        if (!model.containsAttribute("form")) {
            var f = new CreateUserForm();
            f.setRole("USER");
            model.addAttribute("form", f);
        }
        model.addAttribute("user", current);
        return "admin/users-new";
    }

    // CREAR USUARIO
    @PostMapping
    public String create(@ModelAttribute("form") @Valid CreateUserForm form, Model model) {
        var err = new StringBuilder();

        var req = new AuthClient.CreateUserForm(
                form.getEmail(),
                form.getName(),
                form.getRole(),
                form.getPassword()
        );

        boolean ok = auth.createUser(req, err);

        if (!ok) {
            model.addAttribute("error", err.toString());
            model.addAttribute("form", form);
            model.addAttribute("user", current);
            return "admin/users-new";
        }

        return "redirect:/admin/users";
    }

    // ELIMINAR USUARIO
    @PostMapping("/{userId}/delete")
    public String delete(@PathVariable String userId, Model model) {
        var err = new StringBuilder();
        boolean ok = auth.deleteUser(userId, err);
        if (!ok) {
            model.addAttribute("error", err.toString());
        }
        return "redirect:/admin/users";
    }

    // CAMBIAR ROL DE USUARIO
    @PostMapping("/{userId}/role")
    public String changeRole(@PathVariable String userId, @RequestParam String role, Model model) {
        var err = new StringBuilder();
        boolean ok = auth.changeUserRole(userId, role, err);
        if (!ok) {
            model.addAttribute("error", err.toString());
        }
        return "redirect:/admin/users";
    }
}
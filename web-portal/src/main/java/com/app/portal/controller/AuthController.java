package com.app.portal.controller;

import com.app.portal.service.AuthClient;
import com.app.portal.session.CurrentUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Validated
public class AuthController {

    record LoginForm(@Email String email, @NotBlank String password) {}

    private final AuthClient auth;
    private final CurrentUser current;

    public AuthController(AuthClient auth, CurrentUser current) {
        this.auth = auth;
        this.current = current;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm("", ""));
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute("loginForm") LoginForm form,
                          RedirectAttributes ra) {
        var user = auth.login(form.email(), form.password());
        if (user == null) {
            ra.addFlashAttribute("error", "Credenciales inválidas.");
            ra.addFlashAttribute("loginForm", new LoginForm(form.email(), ""));
            return "redirect:/login";
        }
        current.set(user);
        // Redirige a algo visible tras login (cámbialo si quieres)
        return "redirect:/admin/users";
    }

    @PostMapping("/logout")
    public String logout(RedirectAttributes ra) {
        current.clear();
        ra.addFlashAttribute("info", "Sesión cerrada.");
        return "redirect:/login";
    }
}
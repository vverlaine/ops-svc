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

/**
 * Controlador encargado del flujo de autenticación en el portal (login/logout).
 */
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

    /**
     * Muestra el formulario de inicio de sesión y prepara el modelo si es necesario.
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm("", ""));
        }
        return "login";
    }

    /**
     * Procesa el intento de inicio de sesión contra el servicio remoto y maneja mensajes flash.
     */
    @PostMapping("/login")
    public String doLogin(@ModelAttribute("loginForm") LoginForm form, RedirectAttributes ra) {
        var user = auth.login(form.email(), form.password());
        if (user == null) {
            ra.addFlashAttribute("error", "Credenciales inválidas.");
            ra.addFlashAttribute("loginForm", new LoginForm(form.email(), ""));
            return "redirect:/login";
        }

        current.set(user);
        return "redirect:/dashboard";
    }

    /**
     * Cierra la sesión activa del usuario y redirige al formulario de login.
     */
    @PostMapping("/logout")
    public String logout(RedirectAttributes ra) {
        current.clear();
        ra.addFlashAttribute("info", "Sesión cerrada correctamente.");
        return "redirect:/login";
    }
}

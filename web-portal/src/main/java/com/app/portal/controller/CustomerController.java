package com.app.portal.controller;

import com.app.portal.client.CustomerClient;
import com.app.portal.forms.CustomerForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class CustomerController {

    private final CustomerClient customerClient;

    @GetMapping
    public String listClientes(CustomerForm form, Model model) {
        var clientes = customerClient.listCustomers();
        model.addAttribute("clientes", clientes);
        return "clientes";
    }

    @PostMapping
    public String crearCliente(@Valid CustomerForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", customerClient.listCustomers());
            return "clientes";
        }

        boolean ok = customerClient.createCustomer(form);
        if (!ok) {
            result.rejectValue("email", null, "Error al crear cliente");
            model.addAttribute("clientes", customerClient.listCustomers());
            return "clientes";
        }

        redirectAttributes.addFlashAttribute("success", "Cliente creado exitosamente.");
        return "redirect:/clientes";
    }

    @PostMapping("/delete/{id}")
    public String eliminarCliente(@PathVariable UUID id) {
        customerClient.deleteCustomer(id);
        return "redirect:/clientes";
    }
}

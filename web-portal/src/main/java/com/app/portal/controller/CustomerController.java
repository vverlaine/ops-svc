package com.app.portal.controller;

import com.app.portal.client.CustomerClient;
import com.app.portal.forms.CustomerForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

/**
 * Controlador MVC que gestiona la vista de clientes dentro del portal.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class CustomerController {

    private final CustomerClient customerClient;

    /**
     * Muestra el listado de clientes y prepara el formulario vacío o precargado.
     */
    @GetMapping
    public String listClientes(CustomerForm form, Model model) {
        var clientes = customerClient.listCustomers();
        if (!model.containsAttribute("customerForm")) {
            model.addAttribute("customerForm", form);
        }
        model.addAttribute("clientes", clientes);
        return "clientes";
    }

    /**
     * Crea o actualiza un cliente según si el formulario contiene identificador.
     * Maneja validaciones y mensajes de retroalimentación en la vista.
     */
    @PostMapping
    public String guardarCliente(@Valid @ModelAttribute("customerForm") CustomerForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", customerClient.listCustomers());
            return "clientes";
        }

        boolean ok;
        if (form.getId() == null) {
            ok = customerClient.createCustomer(form);
            if (!ok) {
                result.rejectValue("email", null, "Error al crear cliente");
                model.addAttribute("clientes", customerClient.listCustomers());
                return "clientes";
            }
            redirectAttributes.addFlashAttribute("success", "Cliente creado exitosamente.");
        } else {
            ok = customerClient.updateCustomer(form.getId(), form);
            if (!ok) {
                result.rejectValue("email", null, "Error al actualizar cliente");
                model.addAttribute("clientes", customerClient.listCustomers());
                return "clientes";
            }
            redirectAttributes.addFlashAttribute("success", "Cliente actualizado correctamente.");
        }

        return "redirect:/clientes";
    }

    /**
     * Elimina un cliente existente y redirige al listado.
     */
    @PostMapping("/delete/{id}")
    public String eliminarCliente(@PathVariable UUID id) {
        customerClient.deleteCustomer(id);
        return "redirect:/clientes";
    }

    /**
     * Recupera los datos de un cliente para prellenar el formulario de edición.
     */
    @GetMapping("/edit/{id}")
    public String editarCliente(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        Map<String, Object> data = customerClient.getCustomerById(id);
        if (data == null) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }

        CustomerForm form = new CustomerForm();
        form.setId(id);
        form.setName((String) data.get("name"));
        form.setTaxId((String) data.get("taxId"));
        form.setEmail((String) data.get("email"));
        form.setPhone((String) data.get("phone"));
        form.setAddress((String) data.get("address"));

        redirectAttributes.addFlashAttribute("customerForm", form);
        redirectAttributes.addFlashAttribute("editing", true);
        return "redirect:/clientes";
    }
}

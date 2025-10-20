package com.app.portal.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerForm {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;
}
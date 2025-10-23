package com.app.portal.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerForm {

    private UUID id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "Máximo 200 caracteres")
    private String name;

    @Size(max = 50, message = "Máximo 50 caracteres")
    private String taxId;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    @Size(max = 200, message = "Máximo 200 caracteres")
    private String email;

    @Size(max = 50, message = "Máximo 50 caracteres")
    private String phone;

    @Size(max = 255, message = "Máximo 255 caracteres")
    private String address;
}

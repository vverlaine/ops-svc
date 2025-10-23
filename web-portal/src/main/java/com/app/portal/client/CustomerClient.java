package com.app.portal.client;

import com.app.portal.forms.CustomerForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerClient {

    private final RestTemplate restTemplate;

    @Value("${customers.url}")
    private String customersSvcUrl;

    public List<Map<String, Object>> listCustomers() {
        Map<String, Object> response = restTemplate.getForObject(customersSvcUrl + "/customers", Map.class);
        if (response == null) return List.of();
        Object content = response.get("content");
        if (content instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    public boolean createCustomer(CustomerForm form) {
        try {
            restTemplate.postForEntity(customersSvcUrl + "/customers", form, Void.class);
            return true;
        } catch (Exception e) {
            log.error("Error al crear cliente", e);
            return false;
        }
    }

    public boolean updateCustomer(UUID id, CustomerForm form) {
        try {
            Map<String, Object> payload = new HashMap<>();
            if (form.getName() != null) payload.put("name", form.getName());
            if (form.getTaxId() != null) payload.put("taxId", form.getTaxId());
            if (form.getEmail() != null) payload.put("email", form.getEmail());
            if (form.getPhone() != null) payload.put("phone", form.getPhone());
            if (form.getAddress() != null) payload.put("address", form.getAddress());

            restTemplate.patchForObject(customersSvcUrl + "/customers/" + id, payload, Void.class);
            return true;
        } catch (Exception e) {
            log.error("Error al actualizar cliente {}", id, e);
            return false;
        }
    }

    public boolean deleteCustomer(UUID id) {
        String url = customersSvcUrl + "/customers/" + id;
        restTemplate.delete(url);
        return true;
    }

    public Map<String, Object> getCustomerById(UUID id) {
        try {
            String url = customersSvcUrl + "/customers/" + id;
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Error al obtener cliente por ID", e);
            return null;
        }
    }
}

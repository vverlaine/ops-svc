package com.app.portal.client;

import com.app.portal.forms.CustomerForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

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
        return (List<Map<String, Object>>) response.get("content");
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

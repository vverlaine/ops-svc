package com.app.portal.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.app.portal.dto.UserDto;

@Service
public class AuthClient {

    public record CreateUserForm(String email, String name, String role, String password) {
    }

    @Value("${auth.base-url}")
    String baseUrl;

    final RestTemplate rest = new RestTemplate();

    public UserDto login(String email, String password) {
        String url = baseUrl + "/auth/login";
        var req = Map.of("email", email, "password", password);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(req, headers);
            ResponseEntity<UserDto> res = rest.postForEntity(url, entity, UserDto.class);
            return res.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            return null; // credenciales inválidas
        } catch (RestClientException e) {
            // si auth-svc cae, tratar como inválido (y mostrar error genérico si quieres)
            return null;
        }
    }

    public boolean createUser(CreateUserForm form, StringBuilder err) {
        String url = baseUrl + "/auth/register";
        try {
            var res = rest.postForEntity(url, form, Map.class);
            return res.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.BadRequest e) {
            err.append(e.getResponseBodyAsString());
            return false;
        } catch (RestClientException e) {
            err.append("No se pudo contactar auth-svc");
            return false;
        }
    }

    public List<Map<String, Object>> listUsers() {
        String url = baseUrl + "/auth/users";
        ResponseEntity<List> res = rest.exchange(url, HttpMethod.GET, null, List.class);
        return res.getBody();
    }

    public boolean deleteUser(String userId, StringBuilder err) {
        try {
            rest.delete(baseUrl + "/auth/users/" + userId);
            return true;
        } catch (Exception e) {
            err.append("No se pudo eliminar el usuario.");
            return false;
        }
    }

    public boolean changeUserRole(String userId, String role, StringBuilder err) {
        try {
            rest.put(baseUrl + "/auth/users/" + userId + "/role?role=" + role, null);
            return true;
        } catch (HttpClientErrorException.BadRequest e) {
            err.append("Rol inválido.");
            return false;
        } catch (Exception e) {
            err.append("No se pudo cambiar el rol.");
            return false;
        }
    }
}

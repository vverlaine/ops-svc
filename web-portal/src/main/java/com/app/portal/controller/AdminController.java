package com.app.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.app.portal.client.SupervisorClient;
import com.app.portal.service.AuthClient;
import com.app.portal.service.AuthClient.CreateUserForm;
import com.app.portal.dto.UserDto;
import com.app.portal.session.CurrentUser;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final AuthClient auth;
    private final SupervisorClient supervisorClient;
    private final CurrentUser current;

    @Autowired
    public AdminController(AuthClient auth, SupervisorClient supervisorClient, CurrentUser current) {
        this.auth = auth;
        this.supervisorClient = supervisorClient;
        this.current = current;
    }

    @GetMapping("/admin/users")
    public String listarUsuarios(Model model) {
        List<Map<String, Object>> rawUsers = auth.listUsers();
        var supervisorsOptions = supervisorClient.listSupervisors();
        Map<String, String> supervisorNameMap = supervisorsOptions.stream()
                .collect(Collectors.toMap(
                        SupervisorClient.SupervisorOption::id,
                        SupervisorClient.SupervisorOption::name,
                        (a, b) -> a
                ));
        Map<String, String> teamSupervisorIdMap = supervisorsOptions.stream()
                .filter(opt -> opt.teamId() != null)
                .collect(Collectors.toMap(SupervisorClient.SupervisorOption::teamId,
                        SupervisorClient.SupervisorOption::id, (a, b) -> a));

        List<UserDto> usuarios = rawUsers.stream().map(map -> {
            System.out.println("Auth raw user: " + map);
            UserDto dto = new UserDto();
            Object idObj = map.get("id");
            if (idObj != null) {
                try {
                    dto.setId(UUID.fromString(idObj.toString()));
                } catch (IllegalArgumentException e) {
                    dto.setId(null);
                }
            }
            dto.setEmail((String) map.get("email"));
            dto.setName((String) map.get("name"));
            dto.setRole((String) map.get("role"));
            Object supId = map.get("supervisorId");
            dto.setSupervisorId(supId == null ? null : supId.toString());
            Object teamId = map.get("teamId");
            dto.setTeamId(teamId == null ? null : teamId.toString());
            if (dto.getSupervisorId() == null && dto.getTeamId() != null) {
                dto.setSupervisorId(teamSupervisorIdMap.get(dto.getTeamId()));
            }
            return dto;
        }).collect(Collectors.toList());

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("supervisores", supervisorsOptions);
        model.addAttribute("supervisorMap", supervisorNameMap);
        return "/admin/users";
    }

    @GetMapping("/admin/crear")
    public String showCreateUserForm(Model model) {
        model.addAttribute("form", new CreateUserForm("", "", "", "", ""));
        model.addAttribute("supervisores", supervisorClient.listSupervisors());
        return "admin/users-new"; // tu template HTML Thymeleaf
    }

    @PostMapping("/admin/crear")
    public String createUser(
            @ModelAttribute("form") CreateUserForm form,
            Model model
    ) {
        var err = new StringBuilder();
        boolean ok = auth.createUser(form, err);

        if (!ok) {
            model.addAttribute("error", err.toString());
            model.addAttribute("supervisores", supervisorClient.listSupervisors());
            return "admin/users-new";
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/admin/tecnico/supervisor")
    public String changeTechnicianSupervisor(
            @RequestParam String userId,
            @RequestParam(required = false) String supervisorId,
            Model model) {
        var err = new StringBuilder();
        boolean ok = auth.changeTechnicianSupervisor(userId, supervisorId, err);
        if (!ok) {
            model.addAttribute("error", err.toString());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("admin/eliminar")
    public String deleteUser(@RequestParam String userId, Model model) {
        var err = new StringBuilder();
        var ok = auth.deleteUser(userId, err);

        if (!ok) {
            model.addAttribute("error", err.toString());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("admin/rol")
    public String changeRole(@RequestParam String userId,
            @RequestParam String role,
            Model model) {
        var err = new StringBuilder();
        var ok = auth.changeUserRole(userId, role, err);

        if (!ok) {
            model.addAttribute("error", err.toString());
        }

        return "redirect:/admin/users";
    }
}

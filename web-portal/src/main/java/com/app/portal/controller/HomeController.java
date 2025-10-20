package com.app.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "dashboard";
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "dashboard";
    }

    @GetMapping("/skynet")
    public String skynet() {
        return "dashboard";
    }
}
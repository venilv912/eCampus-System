package com.ecampus.controller.faculty;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faculty")
public class FacultyDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "faculty-dashboard"; // can be a dummy html
    }
}

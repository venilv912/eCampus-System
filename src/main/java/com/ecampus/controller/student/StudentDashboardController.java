package com.ecampus.controller.student;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "student-dashboard";
    }
}

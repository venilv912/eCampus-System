package com.ecampus.controller.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import com.ecampus.model.*;
import com.ecampus.service.*;
import com.ecampus.repository.*;

@Controller
@RequestMapping("/admin/computeResult")
public class ComputeResultController {

    @Autowired
    private TermsRepository termRepo;

    @Autowired
    private SemestersRepository semRepo;

    @Autowired
    private ComputeResultService computeResultService;

    @Autowired
    private UserRepository userRepo;

    @GetMapping
    public String chooseSemester(Model model) {
        Long trmid = termRepo.findMaxTrmid();
        List<Semesters> sem = semRepo.findByTrmid(trmid);

        model.addAttribute("semesters", sem);

        return "admin/compute-result/chooseSemester";
    }

    @PostMapping("/process")
    @Transactional
    public String processComputeResult(@RequestParam("semesterId") Long semId, Authentication authentication,RedirectAttributes ra) {
        
        // Your logic to compute results for the specific semester
        System.out.println("Computing results for Semester ID: " + semId);

        String username = authentication.getName();
        Users user = userRepo.findByUname(username).orElseThrow(() -> new RuntimeException("User not found"));

        Semesters currSem = semRepo.findById(semId).orElseThrow(() -> new RuntimeException("Semester not found"));

        if(currSem.getStrfield2().equals("T")){
            ra.addFlashAttribute("success", "Results already computed for the selected semester.");
            return "redirect:/admin/computeResult";
        }
        computeResultService.computeResult(semId);
        
        currSem.setStrlastupdatedby(user.getUid());
        currSem.setStrlastupdatedat(LocalDateTime.now());
        currSem.setStrfield2("T");
        semRepo.save(currSem);
        
        // add success message or logic
        ra.addFlashAttribute("success", "Results computed successfully for the selected semester.");
        
        return "redirect:/admin/computeResult";
    }

}

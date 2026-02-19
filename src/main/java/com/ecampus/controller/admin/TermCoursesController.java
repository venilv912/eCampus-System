package com.ecampus.controller.admin;

import com.ecampus.dto.*;
import com.ecampus.model.*;
import com.ecampus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/term-courses")
public class TermCoursesController {

    @Autowired
    private TermCoursesRepository termCourseRepository;

    @Autowired
    private TermsRepository termRepository;

    @Autowired
    private AcademicYearsRepository academicYearRepository;

    // LIST TERM COURSES (DESC ORDER BY termId, then courseCode)
    @GetMapping
    public String listTermCourses(Model model) {
        List<Object[]> rows = termCourseRepository.getAllTermCoursesDetailsRaw();

        List<TermCoursesViewDTO> termcourses = rows.stream()
                .map(r -> new TermCoursesViewDTO(
                        (String) r[0],
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        (String) r[4]
                )).toList();

        Map<String, Map<String, List<TermCoursesViewDTO>>> coursesByAcademicYearThenTerm =
                termcourses.stream()
                        .collect(Collectors.groupingBy(
                                TermCoursesViewDTO::ayrname,
                                LinkedHashMap::new,
                                Collectors.groupingBy(
                                        TermCoursesViewDTO::term,
                                        LinkedHashMap::new,
                                        Collectors.toList()
                                )
                        ));

        // Build a map of (ayrname, trmname) -> trmid for use in pull electives
        Map<String, Map<String, Long>> termIdMap = new LinkedHashMap<>();
        List<Object[]> termDetails = termRepository.getAllTermsDetailsRaw();
        for (Object[] row : termDetails) {
            Long trmid = ((Number) row[0]).longValue();
            String trmname = (String) row[1];
            String ayrname = (String) row[2];
            
            termIdMap.computeIfAbsent(ayrname, k -> new LinkedHashMap<>()).put(trmname, trmid);
        }

        model.addAttribute("coursesByAcademicYearThenTerm", coursesByAcademicYearThenTerm);
        model.addAttribute("termIdMap", termIdMap);
        return "admin/termcourses";
    }

    // PULL ELECTIVES from previous year's same term
    @PostMapping("/pull-electives")
    @Transactional
    public String pullElectives(
            @RequestParam("termId") Long termId,
            RedirectAttributes redirectAttributes) {

        // 1. Get the current term
        Terms currentTerm = termRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found: " + termId));

        String termName = currentTerm.getTrmname();
        Long currentAyrid = currentTerm.getTrmayrid();

        // 2. Find previous academic year (ayrid - 1)
        Long previousAyrid = currentAyrid - 1;
        
        // Check if previous academic year exists
        AcademicYears previousAcademicYear = academicYearRepository.findById(previousAyrid).orElse(null);
        if (previousAcademicYear == null) {
            redirectAttributes.addFlashAttribute("error", "No previous academic year found");
            return "redirect:/admin/term-courses";
        }

        // 3. Find the term from previous academic year with same term name
        Terms previousTerm = termRepository.findByTrmayridAndTrmname(previousAyrid, termName);
        if (previousTerm == null) {
            redirectAttributes.addFlashAttribute("error", 
                "No " + termName + " term found in previous academic year (" + previousAcademicYear.getAyrname() + ")");
            return "redirect:/admin/term-courses";
        }

        // 4. Get all elective courses from previous term
        List<TermCourses> previousElectives = termCourseRepository.findByTcrtrmidAndCrstype(
                previousTerm.getTrmid(), "ELECTIVE");

        if (previousElectives.isEmpty()) {
            redirectAttributes.addFlashAttribute("warning", 
                "No electives found in " + termName + " of " + previousAcademicYear.getAyrname());
            return "redirect:/admin/term-courses";
        }

        // 5. Copy electives to current term (skip if already exists)
        int addedCount = 0;
        for (TermCourses prevTc : previousElectives) {
            Long crsid = prevTc.getTcrcrsid();
            
            // Check if this course already exists in current term
            boolean exists = termCourseRepository.existsByTcrtrmidAndTcrcrsid(termId, crsid);
            if (!exists) {
                // Generate new tcrid
                Long maxTcrid = termCourseRepository.findMaxTcrid();
                Long newTcrid = (maxTcrid == null ? 0 : maxTcrid) + 1;

                // Create new TermCourse entry
                TermCourses newTc = new TermCourses();
                newTc.setTcrid(newTcrid);
                newTc.setTcrtrmid(termId);
                newTc.setTcrcrsid(crsid);
                newTc.setTcrtype(prevTc.getTcrtype());
                newTc.setTcrroundlogic(prevTc.getTcrroundlogic());
                newTc.setTcrmarks(prevTc.getTcrmarks());
                newTc.setTcrstatus("AVAILABLE");
                newTc.setCrstype("ELECTIVE");
                newTc.setTcrrowstate(1L);
                newTc.setTcrcreatedat(LocalDateTime.now());
                
                termCourseRepository.save(newTc);
                addedCount++;
            }
        }

        if (addedCount > 0) {
            redirectAttributes.addFlashAttribute("success", 
                addedCount + " elective(s) pulled from " + termName + " of " + previousAcademicYear.getAyrname());
        } else {
            redirectAttributes.addFlashAttribute("info", 
                "All electives from previous year already exist in current term");
        }

        return "redirect:/admin/term-courses";
    }
}

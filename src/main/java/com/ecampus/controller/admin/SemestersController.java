package com.ecampus.controller.admin;

import com.ecampus.dto.*;
import com.ecampus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class SemestersController {

    @Autowired
    private SemestersRepository semesterRepository;

//    @Autowired
//    private SemesterCoursesRepository semesterCoursesRepository;

    // LIST SEMESTERS (DESC ORDER)
    @GetMapping("/semesters")
    public String listSemesters(Model model) {
        List<Object[]> rows = semesterRepository.getAllSemestersDetailsRaw();

        List<SemesterViewDTO> semesters = rows.stream()
                .map(r -> new SemesterViewDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3]
                )).toList();

        Map<String, List<SemesterViewDTO>> semestersByTerm =
                semesters.stream()
                        .collect(Collectors.groupingBy(
                                SemesterViewDTO::term,
                                LinkedHashMap::new, // preserves the order
                                Collectors.toList()
                        ));

        model.addAttribute("semestersByTerm", semestersByTerm);
        return "admin/semesters"; // semesters.html
    }

//    @GetMapping("/semestercourses")
//    public String listSemesterCourses(Model model) {
//        List<Object[]> rows = semesterCoursesRepository.getAllSemesterCoursesDetailsRaw();
//
//        List<SemesterCoursesViewDTO> semesterCourses = rows.stream()
//                .map(r -> new SemesterCoursesViewDTO(
//                        (String) r[0],
//                        (String) r[1],
//                        (String) r[2],
//                        (String) r[3],
//                        (String) r[4],
//                        (String) r[5]
//                )).toList();
//
//        Map<String, Map<String, List<SemesterCoursesViewDTO>>> coursesByTermThenBatch =
//                semesterCourses.stream()
//                                .collect(Collectors.groupingBy(
//                                        SemesterCoursesViewDTO::term,
//                                        LinkedHashMap::new,
//                                        Collectors.groupingBy(
//                                                SemesterCoursesViewDTO::batch,
//                                                LinkedHashMap::new,
//                                                Collectors.toList()
//                                        )
//                                ));
//
//        model.addAttribute("coursesByTermThenBatch", coursesByTermThenBatch);
//        return "admin/semestercourses";
//    }
}

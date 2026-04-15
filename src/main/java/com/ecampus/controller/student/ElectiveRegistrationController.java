package com.ecampus.controller.student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecampus.model.*;
import com.ecampus.repository.*;
import com.ecampus.service.*;

@Controller
public class ElectiveRegistrationController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StudentRegistrationService registrationService;

    @Autowired
    private SemestersRepository semestersRepo;

    @Autowired
    private RegistrationOpenForRepository registrationOpenForRepo;

    @Autowired
    private TermCoursesRepository termCoursesRepo;

    @Autowired
    private CoursePreferencesRepository coursePrefRepo;

    @Autowired
    private SlotPreferencesRepository slotPrefRepo;

    @Autowired
    private StudentCourseRequirementsRepository studCourseReqRepo;
    
    @GetMapping("/student/electiveRegistration")
    public String getcourses(Authentication authentication, Model model, RedirectAttributes redirectAttributes){

        String username = authentication.getName();

        Long studentId = userRepo.findIdByUname(username);

        Students st = registrationService.getStudentById(studentId);

        Long trmid = semestersRepo.findMaxTrmIdByBchId(st.getStdbchid());

        RegistrationOpenFor rof = registrationOpenForRepo.getRofByTrmBch(trmid,st.getStdbchid(),"Elective");

        if(rof==null || LocalDateTime.now().isBefore(rof.getStartdate())){
            redirectAttributes.addFlashAttribute("error", "Elective Registration is not yet open.");
            return "redirect:/student/dashboard";
        }

        if(LocalDateTime.now().isAfter(rof.getEnddate())){
            redirectAttributes.addFlashAttribute("error", "Elective Registration has ended.");
            return "redirect:/student/dashboard";
        }

        List<Object[]> results = termCoursesRepo.findCoursesBySlot(trmid, "ELECTIVE", st.getStdbchid());

        List<String> electiveTypes = results.stream()
                                        .map(row -> (String) row[6])   // get index 6
                                        .filter(Objects::nonNull)      // avoid nulls
                                        .distinct()                    // remove duplicates
                                        .collect(Collectors.toList());

        // Grouping by tcrslot (which is at index 7 in our query)
        Map<Object, List<Object[]>> slotMap = results.stream()
                .collect(Collectors.groupingBy(row -> row[7]));

        model.addAttribute("slotMap", slotMap);
        model.addAttribute("electiveTypes", electiveTypes);

        return "student/electiveRegistration";
    }

    @PostMapping("/student/submitPreference")
    @Transactional
    public String handleSubmission(@RequestParam Map<String, String> allParams, Authentication authentication, Model model) {
        
        // 1. To store: Map<SlotNumber, List<TcrId>> (Sorted by Rank)
        // Temporary helper to store {SlotNo -> {Rank -> TcrId}}
        Map<Long, Map<Long, Long>> tempSlotMap = new HashMap<>();           // course_prefs

        // 2. To store: Map<SlotNumber, PriorityRank>
        Map<Long, Long> slotPriorities = new HashMap<>();                   // slot_prefs

        // 3. To store: Map<ElectiveType, Count>
        Map<String, Long> electiveRequirements = new HashMap<>();              // requirements

        // Iterate through all incoming parameters
        allParams.forEach((key, value) -> {
            if (value == null || value.trim().isEmpty()) return;

            try {
                if (key.startsWith("pref_")) {
                    // Key Format: pref_{slotNo}_{tcrId}
                    String[] parts = key.split("_");
                    Long slotNo = Long.parseLong(parts[1]);
                    Long tcrId = Long.parseLong(parts[2]);
                    Long rank = Long.parseLong(value);

                    tempSlotMap.computeIfAbsent(slotNo, k -> new TreeMap<>()).put(rank, tcrId);

                } else if (key.startsWith("slotPriority_")) {
                    // Key Format: slotPriority_{slotNo}
                    Long slotNo = Long.parseLong(key.split("_")[1]);
                    slotPriorities.put(slotNo, Long.parseLong(value));

                } else if (key.startsWith("count_")) {
                    // Key Format: count_{electiveType}
                    String type = key.replace("count_", "");
                    electiveRequirements.put(type, Long.parseLong(value));
                }
            } catch (Exception e) {
                System.err.println("Error parsing key: " + key + " with value: " + value);
            }
        });

        // --- DEBUG LOGS ---
        System.out.println("1. Course Preferences: " + tempSlotMap);
        System.out.println("2. Slot Priorities: " + slotPriorities);
        System.out.println("3. Elective Requirements: " + electiveRequirements);

        String username = authentication.getName();
        Long studentId = userRepo.findIdByUname(username);

        List<StudentCourseRequirements> stdCrsReq = studCourseReqRepo.findBySid(studentId);

        if(!stdCrsReq.isEmpty()){
            coursePrefRepo.deleteBySid(studentId);
            slotPrefRepo.deleteBySid(studentId);
            studCourseReqRepo.deleteBySid(studentId);
        }

        List<StudentCourseRequirements> scrlist = new ArrayList<>();
        for (Map.Entry<String, Long> entry : electiveRequirements.entrySet()) {
            String electiveType = entry.getKey();
            Long count = entry.getValue();

            StudentCourseRequirements scr = new StudentCourseRequirements();
            scr.setSid(studentId);
            scr.setElectType(electiveType);
            scr.setCount(count);
            scrlist.add(scr);
        }
        studCourseReqRepo.saveAll(scrlist);

        List<SlotPreferences> slotPreflist = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : slotPriorities.entrySet()) {
            Long slot = entry.getKey();
            Long priority = entry.getValue();

            SlotPreferences slotPref = new SlotPreferences();
            slotPref.setSid(studentId);
            slotPref.setPrefIndex(priority);
            slotPref.setSlot(slot);
            slotPreflist.add(slotPref);
        }
        slotPrefRepo.saveAll(slotPreflist);

        List<CoursePreferences> crsPreflist = new ArrayList<>();
        for (Map.Entry<Long, Map<Long, Long>> slotEntry : tempSlotMap.entrySet()) {
            Long slot = slotEntry.getKey();
            Map<Long, Long> innerMap = slotEntry.getValue();

            for (Map.Entry<Long, Long> prefEntry : innerMap.entrySet()) {
                Long prefIndex = prefEntry.getKey();
                Long courseId = prefEntry.getValue();

                CoursePreferences crsPref = new CoursePreferences();
                crsPref.setSid(studentId);
                crsPref.setPrefIndex(prefIndex);
                crsPref.setSlot(slot);
                crsPref.setTcrid(courseId);
                crsPreflist.add(crsPref);
            }
        }
        coursePrefRepo.saveAll(crsPreflist);

        return "redirect:/student/electiveRegistration/view";
    }

    @GetMapping("/student/electiveRegistration/view")
    public String viewRegistration(Authentication authentication, Model model, RedirectAttributes redirectAttributes){

        String username = authentication.getName();

        Long studentId = userRepo.findIdByUname(username);

        List<Object[]> requirements = studCourseReqRepo.getBySid(studentId);
        List<Object[]> slotPref = slotPrefRepo.getBySid(studentId);
        List<CoursePreferences> coursePref = coursePrefRepo.getBySid(studentId);

        if(requirements==null || slotPref==null || coursePref==null){
            redirectAttributes.addFlashAttribute("error", "No Elective Registration record found.");
            return "redirect:/student/dashboard";
        }

        Map<Long, List<CoursePreferences>> grouped = coursePref.stream()
        .collect(Collectors.groupingBy(
            CoursePreferences::getSlot,   // group by slot
            Collectors.collectingAndThen(Collectors.toList(), list -> {
                list.sort(Comparator.comparing(CoursePreferences::getPrefIndex)); // sort by pref_index
                return list;
            })
        ));

        model.addAttribute("groupedCoursePref", grouped);
        model.addAttribute("slotPref", slotPref);
        model.addAttribute("requirements", requirements);

        return "student/viewElectiveRegistration";
    }

}

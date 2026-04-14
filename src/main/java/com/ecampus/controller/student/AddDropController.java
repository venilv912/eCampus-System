package com.ecampus.controller.student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import com.ecampus.model.*;
import com.ecampus.repository.*;
import com.ecampus.service.*;

@Controller
public class AddDropController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StudentRegistrationService registrationService;

    @Autowired
    private TermsRepository termRepo;

    @Autowired
    private SemestersRepository semRepo;

    @Autowired
    private StudentRegistrationsRepository stdRegRepo;

    @Autowired
    private StudentRegistrationCoursesRepository stdRegCrsRepo;

    @Autowired
    private TermCourseAvailableForRepository trmCrsAvailableForRepo;

    @Autowired
    private TermCoursesRepository trmCrsRepo;

    @Autowired
    private AddDropPreferencesRepository addDropPrefRepo;

    @Autowired
    private CoursesRepository crsRepo;

    @GetMapping("/student/addDrop")
    public String getcourses(Authentication authentication, Model model){

        String username = authentication.getName();

        Long studentId = userRepo.findIdByUname(username);

        Students st = registrationService.getStudentById(studentId);

        Long trmid = termRepo.findMaxTrmid();
        Long strid = semRepo.findSemByBchAndTrm(st.getStdbchid(),trmid);
        StudentRegistrations stdReg = stdRegRepo.findByStudentIdAndSemesterId(studentId, strid);

        List<Long> electRegistered = stdRegCrsRepo.findElectivesForStud(stdReg.getSrgid());
        List<Long> remainingCrs = trmCrsAvailableForRepo.findByTrmAndBch(trmid,st.getStdbchid());
        remainingCrs.removeAll(electRegistered);

        List<Object[]> canDrop = trmCrsRepo.findCrsByListTcrids(electRegistered);
        List<Object[]> canAdd = trmCrsRepo.findCrsByListTcrids(remainingCrs);
        
        model.addAttribute("canDrop", canDrop);
        model.addAttribute("canAdd", canAdd);

        return "student/addDrop";
    }

    @PostMapping("/student/submitAddDrop")
    @Transactional
    public String processAddDrop(
            @RequestParam("addCount") Long addCount,
            @RequestParam(value = "addTcrids", required = false) List<String> addTcrids,
            @RequestParam(value = "dropId1", required = false) String dropId1,
            @RequestParam(value = "replaceIds1", required = false) List<String> replaceIds1,
            @RequestParam(value = "dropId2", required = false) String dropId2,
            @RequestParam(value = "replaceIds2", required = false) List<String> replaceIds2,
            @RequestParam(value = "dropId3", required = false) String dropId3,
            @RequestParam(value = "replaceIds3", required = false) List<String> replaceIds3,
            Authentication authentication
    ) {

        System.out.println("Add Count: " + addCount);
        System.out.println("Add List: " + addTcrids);
        System.out.println("Drop1: " + dropId1);
        System.out.println("Replace List 1: " + replaceIds1);
        System.out.println("Drop2: " + dropId2);
        System.out.println("Replace List 2: " + replaceIds2);
        System.out.println("Drop3: " + dropId3);
        System.out.println("Replace List 3: " + replaceIds3);

        String username = authentication.getName();
        Long studentId = userRepo.findIdByUname(username);

        AddDropPreferences exist = addDropPrefRepo.findBySid(studentId);
        if(exist!=null){
            exist.setAddcount(addCount);
            exist.setAddp1(addTcrids.get(0));
            exist.setAddp2(addTcrids.get(1));
            exist.setAddp3(addTcrids.get(2));
            exist.setAddp4(addTcrids.get(3));
            exist.setDrop1(dropId1);
            exist.setDrop1_p1(replaceIds1.get(0));
            exist.setDrop1_p2(replaceIds1.get(1));
            exist.setDrop1_p3(replaceIds1.get(2));
            exist.setDrop2(dropId2);
            exist.setDrop2_p1(replaceIds2.get(0));
            exist.setDrop2_p2(replaceIds2.get(1));
            exist.setDrop2_p3(replaceIds2.get(2));
            exist.setDrop3(dropId3);
            exist.setDrop3_p1(replaceIds3.get(0));
            exist.setDrop3_p2(replaceIds3.get(1));
            exist.setDrop3_p3(replaceIds3.get(2));
            addDropPrefRepo.save(exist);
        }
        else{
            AddDropPreferences addDropPref = new AddDropPreferences();
            addDropPref.setSid(studentId);
            addDropPref.setAddcount(addCount);
            addDropPref.setAddp1(addTcrids.get(0));
            addDropPref.setAddp2(addTcrids.get(1));
            addDropPref.setAddp3(addTcrids.get(2));
            addDropPref.setAddp4(addTcrids.get(3));
            addDropPref.setDrop1(dropId1);
            addDropPref.setDrop1_p1(replaceIds1.get(0));
            addDropPref.setDrop1_p2(replaceIds1.get(1));
            addDropPref.setDrop1_p3(replaceIds1.get(2));
            addDropPref.setDrop2(dropId2);
            addDropPref.setDrop2_p1(replaceIds2.get(0));
            addDropPref.setDrop2_p2(replaceIds2.get(1));
            addDropPref.setDrop2_p3(replaceIds2.get(2));
            addDropPref.setDrop3(dropId3);
            addDropPref.setDrop3_p1(replaceIds3.get(0));
            addDropPref.setDrop3_p2(replaceIds3.get(1));
            addDropPref.setDrop3_p3(replaceIds3.get(2));
            addDropPrefRepo.save(addDropPref);
        }

        return "redirect:/student/addDrop/view";
    }

    @GetMapping("/student/addDrop/view")
    public String viewAddDropPreferences(Model model, Authentication authentication) {
        String username = authentication.getName();
        Long studentId = userRepo.findIdByUname(username);

        // 1. Fetch the raw preferences
        AddDropPreferences prefs = addDropPrefRepo.findBySid(studentId);
        
        if (prefs == null) {
            return "redirect:/student/addDrop?error=NoPreferencesFound";
        }

        // 2. We need a way to show Course Names instead of just IDs
        // We will pass the prefs to the model, but we also need to pass 
        // a Map of crscode -> Course Details for the UI to look up
        model.addAttribute("prefs", prefs);
        
        // 3. Fetch details for all selected courses to display them properly
        // This helper method finds all unique crscode in the prefs and gets their info
        model.addAttribute("courseDetails", getCourseDetailMap(prefs));

        return "student/viewAddDrop";
    }

    private Map<String, Object[]> getCourseDetailMap(AddDropPreferences p) {
        List<String> allIds = new ArrayList<>();

        if(p.getAddp1() != null) allIds.add(p.getAddp1());
        if(p.getAddp2() != null) allIds.add(p.getAddp2());
        if(p.getAddp3() != null) allIds.add(p.getAddp3());
        if(p.getAddp4() != null) allIds.add(p.getAddp4());
        if(p.getDrop1() != null) allIds.add(p.getDrop1());
        if(p.getDrop1_p1() != null) allIds.add(p.getDrop1_p1());
        if(p.getDrop1_p2() != null) allIds.add(p.getDrop1_p2());
        if(p.getDrop1_p3() != null) allIds.add(p.getDrop1_p3());
        if(p.getDrop2() != null) allIds.add(p.getDrop2());
        if(p.getDrop2_p1() != null) allIds.add(p.getDrop2_p1());
        if(p.getDrop2_p2() != null) allIds.add(p.getDrop2_p2());
        if(p.getDrop2_p3() != null) allIds.add(p.getDrop2_p3());
        if(p.getDrop3() != null) allIds.add(p.getDrop3());
        if(p.getDrop3_p1() != null) allIds.add(p.getDrop3_p1());
        if(p.getDrop3_p2() != null) allIds.add(p.getDrop3_p2());
        if(p.getDrop3_p3() != null) allIds.add(p.getDrop3_p3());
        
        // Query repository for details: Map<code, Object[]{code, name, ltpc}>
        List<Object[]> details = crsRepo.findCrsByListTcrids(allIds);
        
        Map<String, Object[]> map = new HashMap<>();
        for(Object[] row : details) {
            map.put(row[0].toString(), row);
        }
        return map;
    }
    
}

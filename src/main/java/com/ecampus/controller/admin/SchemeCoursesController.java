package com.ecampus.controller.admin;

import com.ecampus.model.SchemeCourses;
import com.ecampus.model.SchemeCoursesId;
import com.ecampus.model.CourseTypes;
import com.ecampus.model.Courses;
import com.ecampus.model.Programs;
import com.ecampus.util.RomanNumeralUtil;
import com.ecampus.repository.ProgramsRepository;
import com.ecampus.repository.SchemeCoursesRepository;
import com.ecampus.repository.CourseTypesRepository;
import com.ecampus.repository.CoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// Helper class to hold semester metadata for grouping
class SemesterKey implements Comparable<SemesterKey> {
    private Long programYear;
    private String termName;
    private Long termSeqNo;
    private String semesterName;

    public SemesterKey(Long programYear, String termName, Long termSeqNo, String semesterName) {
        this.programYear = programYear;
        this.termName = termName;
        this.termSeqNo = termSeqNo;
        this.semesterName = semesterName;
    }

    public Long getProgramYear() {
        return programYear;
    }

    public String getTermName() {
        return termName;
    }

    public Long getTermSeqNo() {
        return termSeqNo;
    }

    public String getSemesterName() {
        return semesterName;
    }

    @Override
    public int compareTo(SemesterKey other) {
        int cmp = this.programYear.compareTo(other.programYear);
        if (cmp != 0) return cmp;
        return this.termSeqNo.compareTo(other.termSeqNo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemesterKey that)) return false;
        return Objects.equals(programYear, that.programYear) && Objects.equals(termName, that.termName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(programYear, termName);
    }
}

@Controller
@RequestMapping("/admin/programs/{programId}/schemes/{schemeId}/{splid}/scheme-courses")
public class SchemeCoursesController {

    @Autowired
    private SchemeCoursesRepository schemeCoursesRepository;

    @Autowired
    private ProgramsRepository programsRepository;
    @Autowired
    private CourseTypesRepository courseTypeRepository;
    @Autowired
    private CoursesRepository coursesRepository;

    @GetMapping
    public String listSchemeCourses(@PathVariable Long programId,
                                    @PathVariable Long schemeId,
                                    @PathVariable Long splid,
                                    Model model) {
        // include general degree (splid = 0) and the requested specialization
        List<Long> splids = (splid == 0L) ? List.of(0L) : Arrays.asList(0L, splid);

        // Fetch courses sorted by programYear and termSeqNo
        List<SchemeCourses> courses = schemeCoursesRepository.findBySchemeIdAndSplidInOrderByProgramYearAscTermSeqNoAsc(schemeId, splids);

        // Group by SemesterKey (programYear + termName)
        Map<SemesterKey, List<SchemeCourses>> groupedCourses = new TreeMap<>();
        for (SchemeCourses course : courses) {
            SemesterKey key = new SemesterKey(
                    course.getProgramYear(),
                    course.getTermName(),
                    course.getTermSeqNo(),
                    course.getSemesterName()
            );
            groupedCourses.computeIfAbsent(key, k -> new ArrayList<>()).add(course);
        }

        // Sort each list by splid then courseSrNo
        for (List<SchemeCourses> courseList : groupedCourses.values()) {
            courseList.sort(Comparator
                    .comparingLong(SchemeCourses::getSplid)
                    .thenComparingLong(SchemeCourses::getCourseSrNo)
            );
        }

        // Get program duration in years
        Optional<Programs> progOpt = programsRepository.findById(programId);
        long durationYears = 0;
        if (progOpt.isPresent()) {
            Programs prog = progOpt.get();
            long prgdur = prog.getPrgduration() != null ? prog.getPrgduration() : 0;
            String units = prog.getPrgdurationunits();
            if (units != null && units.equalsIgnoreCase("Semesters")) {
                durationYears = prgdur / 2; // semesters -> years
            } else {
                durationYears = prgdur; // assume already in years
            }
        }

        // Generate all semesters (Autumn, Winter, Summer) for each year
        // No Summer for the last year
        int semesterCounter = 1;
        int summerCounter = 1;
        for (int year = 1; year <= durationYears; year++) {
            // Autumn
            String autumnSemName = "Semester " + RomanNumeralUtil.toRoman(semesterCounter);
            SemesterKey autumnKey = new SemesterKey((long) year, "Autumn", 1L, autumnSemName);
            groupedCourses.putIfAbsent(autumnKey, new ArrayList<>());
            semesterCounter++;

            // Winter
            String winterSemName = "Semester " + RomanNumeralUtil.toRoman(semesterCounter);
            SemesterKey winterKey = new SemesterKey((long) year, "Winter", 2L, winterSemName);
            groupedCourses.putIfAbsent(winterKey, new ArrayList<>());
            semesterCounter++;

            // Summer (not for last year)
            if (year < durationYears) {
                String summerSemName = "Summer " + RomanNumeralUtil.toRoman(summerCounter);
                SemesterKey summerKey = new SemesterKey((long) year, "Summer", 3L, summerSemName);
                groupedCourses.putIfAbsent(summerKey, new ArrayList<>());
                summerCounter++;
            }
        }

        model.addAttribute("groupedCourses", groupedCourses);
        model.addAttribute("programId", programId);
        model.addAttribute("schemeId", schemeId);
        model.addAttribute("splid", splid);
        return "admin/scheme-courses";
    }

    @GetMapping("/add")
    public String showAddForm(@PathVariable Long programId,
                              @PathVariable Long schemeId,
                              @PathVariable Long splid,
                              @RequestParam("termName") String termName,
                              @RequestParam("programYear") Long programYear,
                              @RequestParam("semesterName") String semesterName,
                              @RequestParam("termSeqNo") Long termSeqNo,
                              Model model) {

        SchemeCourses sc = new SchemeCourses();
        sc.setSchemeId(schemeId);
        sc.setSplid(splid);
        sc.setTermName(termName);
        sc.setProgramYear(programYear);
        sc.setSemesterName(semesterName);
        sc.setTermSeqNo(termSeqNo);

        // Calculate semNo based on programYear and termName
        // Autumn: (year-1)*2 + 1, Winter: (year-1)*2 + 2
        if ("Autumn".equals(termName)) {
            sc.setSemNo((programYear - 1) * 2 + 1);
        } else if ("Winter".equals(termName)) {
            sc.setSemNo((programYear - 1) * 2 + 2);
        } else {
            sc.setSemNo(null); // Summer has no traditional semNo
        }

        // Fetch existing courses for this term and splid
        List<SchemeCourses> existingCourses = schemeCoursesRepository
                .findBySchemeIdAndSplidAndTermNameAndProgramYearOrderByCourseSrNo(schemeId, splid, termName, programYear);

        // Determine next serial number
        long nextSrNo = existingCourses.stream()
                .mapToLong(SchemeCourses::getCourseSrNo)
                .max()
                .orElse(0) + 1;

        sc.setCourseSrNo(nextSrNo);

        // Course types: only this splid
        List<Long> splids = List.of(splid);
        List<CourseTypes> courseTypes = courseTypeRepository.findBySchemeIdAndSplidIn(schemeId, splids);
        List<Courses> allCourses = coursesRepository.findAll();

        model.addAttribute("schemeCourse", sc);
        model.addAttribute("courseTypes", courseTypes);
        model.addAttribute("masterCourses", allCourses);
        model.addAttribute("programId", programId);
        model.addAttribute("schemeId", schemeId);
        model.addAttribute("splid", splid);
        model.addAttribute("editing", false);

        return "admin/scheme-course-form";
    }

    @PostMapping("/save")
    public String saveSchemeCourse(@PathVariable Long programId,
                                   @PathVariable Long schemeId,
                                   @PathVariable Long splid,
                                   @ModelAttribute("schemeCourse") SchemeCourses sc,
                                   Model model) {
        // ensure composite key fields
        sc.setSchemeId(schemeId);
        sc.setSplid(splid);

        // if adding (courseSrNo might be 0), compute next
        if (sc.getCourseSrNo() == null || sc.getCourseSrNo() == 0L) {
            List<SchemeCourses> existing = schemeCoursesRepository
                    .findBySchemeIdAndSplidAndTermNameAndProgramYearOrderByCourseSrNo(
                            schemeId, splid, sc.getTermName(), sc.getProgramYear());
            long next = existing.stream().mapToLong(SchemeCourses::getCourseSrNo).max().orElse(0) + 1;
            sc.setCourseSrNo(next);
        }

        schemeCoursesRepository.save(sc);
        return "redirect:/admin/programs/" + programId + "/schemes/" + schemeId + "/" + splid + "/scheme-courses";
    }

    @GetMapping("/edit")
    public String showEditForm(@PathVariable Long programId,
                               @PathVariable Long schemeId,
                               @PathVariable Long splid,
                               @RequestParam("termName") String termName,
                               @RequestParam("programYear") Long programYear,
                               @RequestParam("courseSrNo") Long courseSrNo,
                               Model model,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Optional<SchemeCourses> courseOpt = schemeCoursesRepository.findById(
                new SchemeCoursesId(schemeId, splid, termName, programYear, courseSrNo));
        if (courseOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Course not found.");
            return "redirect:/admin/programs/" + programId + "/schemes/" + schemeId + "/" + splid + "/scheme-courses";
        }

        SchemeCourses course = courseOpt.get();

        // Prevent editing general-degree from specialization page
        if (!course.getSplid().equals(splid)) {
            redirectAttributes.addFlashAttribute("error", "This course belongs to the general degree; update it from the general degree page.");
            return "redirect:/admin/programs/" + programId + "/schemes/" + schemeId + "/" + splid + "/scheme-courses";
        }

        // Prepare dropdowns
        List<Long> splids = List.of(splid);
        List<CourseTypes> courseTypes = courseTypeRepository.findBySchemeIdAndSplidIn(schemeId, splids);
        List<Courses> allCourses = coursesRepository.findAll();

        // prepare selected course display (code - name) for the existing crsid
        String selectedCourseDisplay = "";
        if (course.getCrsid() != null) {
            Optional<Courses> mcOpt = coursesRepository.findById(course.getCrsid());
            if (mcOpt.isPresent()) {
                Courses mc = mcOpt.get();
                selectedCourseDisplay = (mc.getCrscode() != null ? mc.getCrscode() : "") + " - " + (mc.getCrsname() != null ? mc.getCrsname() : "");
            }
        }

        model.addAttribute("schemeCourse", course);
        model.addAttribute("courseTypes", courseTypes);
        model.addAttribute("masterCourses", allCourses);
        model.addAttribute("selectedCourseDisplay", selectedCourseDisplay);
        model.addAttribute("programId", programId);
        model.addAttribute("schemeId", schemeId);
        model.addAttribute("splid", splid);
        model.addAttribute("editing", true);

        return "admin/scheme-course-form";
    }

    @GetMapping("/delete")
    public String deleteSchemeCourse(@PathVariable Long programId,
                                     @PathVariable Long schemeId,
                                     @PathVariable Long splid,
                                     @RequestParam("termName") String termName,
                                     @RequestParam("programYear") Long programYear,
                                     @RequestParam("courseSrNo") Long courseSrNo,
                                     org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        SchemeCoursesId id = new SchemeCoursesId(schemeId, splid, termName, programYear, courseSrNo);
        Optional<SchemeCourses> courseOpt = schemeCoursesRepository.findById(id);
        if (courseOpt.isPresent()) {
            SchemeCourses course = courseOpt.get();
            if (!course.getSplid().equals(splid)) {
                redirectAttributes.addFlashAttribute("error", "This course belongs to the general degree; delete it from the general degree page.");
                return "redirect:/admin/programs/" + programId + "/schemes/" + schemeId + "/" + splid + "/scheme-courses";
            }

            schemeCoursesRepository.deleteById(id);

            // resequence remaining courses in this term
            List<SchemeCourses> remaining = schemeCoursesRepository
                    .findBySchemeIdAndSplidAndTermNameAndProgramYearOrderByCourseSrNo(schemeId, splid, termName, programYear);
            for (int i = 0; i < remaining.size(); i++) {
                SchemeCourses c = remaining.get(i);
                long expected = i + 1;
                if (!c.getCourseSrNo().equals(expected)) {
                    schemeCoursesRepository.deleteById(
                            new SchemeCoursesId(schemeId, splid, c.getTermName(), c.getProgramYear(), c.getCourseSrNo()));
                    c.setCourseSrNo(expected);
                    schemeCoursesRepository.save(c);
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Course not found.");
        }

        return "redirect:/admin/programs/" + programId + "/schemes/" + schemeId + "/" + splid + "/scheme-courses";
    }
}

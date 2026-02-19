package com.ecampus.controller.admin;

import com.ecampus.dto.*;
import com.ecampus.util.RomanNumeralUtil;
import com.ecampus.model.*;
import com.ecampus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/terms")
public class TermsController {

    @Autowired
    private ProgramsRepository programsRepository;

    @Autowired
    private TermsRepository termRepository;

    @Autowired
    private AcademicYearsRepository academicYearRepository;

    @Autowired
    private SemestersRepository semesterRepository;

    @Autowired
    private BatchesRepository batchRepository;

    @Autowired
    private SchemeCoursesRepository schemeCoursesRepository;

    @Autowired
    private TermCoursesRepository termCourseRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private SemesterCoursesRepository semesterCoursesRepository;
    
    @Autowired
    private CoursesRepository coursesRepository;

    // Show "Add Term" form
    @GetMapping("/add")
    public String showAddTermForm(Model model) {
        model.addAttribute("academicYears", academicYearRepository.findAllByOrderByAyridDesc());
        model.addAttribute("termNames", List.of("Autumn", "Winter", "Summer"));
        return "admin/term-form";
    }

    // Handle "Add Term" submission
    @PostMapping("/add")
    @Transactional
    public String addTerm(
            @RequestParam("academicYearId") Long academicYearId,
            @RequestParam("termName") String termName,
            @RequestParam("termStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate termStartDate,
            @RequestParam("termEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate termEndDate,
            Model model) {

        // 1. Generate new Term ID (max+1)
        Long maxId = termRepository.findMaxTrmid();
        Long newTermId = maxId + 1;
        
        // 2. Set trmseqno based on termName
        Long trmSeqNo;
        if ("Autumn".equalsIgnoreCase(termName)) {
            trmSeqNo = 1L;
        } else if ("Winter".equalsIgnoreCase(termName)) {
            trmSeqNo = 2L;
        } else if ("Summer".equalsIgnoreCase(termName)) {
            trmSeqNo = 3L;
        } else {
            trmSeqNo = 0L;
        }

        // 3. Get Academic Year entity
        AcademicYears academicYear = academicYearRepository
                .findById(academicYearId)
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));

        // 4. Create and save Term
        Terms term = new Terms();
        term.setAcademicYear(academicYear);
        term.setTrmid(newTermId);
        term.setTrmname(termName);
        term.setTrmseqno(trmSeqNo);
        term.setTrmstarts(termStartDate);
        term.setTrmends(termEndDate);
        term.setTrmrowstate(1L);
        term.setTrmcreatedat(LocalDateTime.now());
        termRepository.save(term);

        // 5. Extract starting year from academic year name (e.g., "2025-26" -> 2025)
        String academicAyrName = academicYear.getAyrname();
        int academicYearStarting = Integer.parseInt(academicAyrName.split("-")[0]);

        // 6. Find all batches and filter to get active batches
        List<Batches> allBatches = batchRepository.findAll();

        List<Batches> activeBatches = allBatches.stream()
                .filter(batch -> {
                    // Skip if schemeId is null
                    if (batch.getSchemeId() == null) return false;

                    Long batchAyrId = batch.getBchcalid();
                    if (batchAyrId == null) return false;

                    Programs program = programsRepository.findById(batch.getBchprgid()).orElse(null);
                    if (program == null || program.getPrgduration() == null) return false;

                    // Get batch's academic year
                    AcademicYears batchYear = academicYearRepository.findById(batchAyrId).orElse(null);
                    if (batchYear == null) return false;

                    String batchAyrName = batchYear.getAyrname();
                    int batchYearStarting = Integer.parseInt(batchAyrName.split("-")[0]);

                    // Calculate year difference
                    int diff = academicYearStarting - batchYearStarting;
                    
                    // Active if diff >= 0 and diff < program duration
                    return diff >= 0 && diff < program.getPrgduration();
                })
                .toList();

        // Map to track TermCourse tcrid by crsid (to avoid duplicates across batches)
        Map<Long, Long> crsidToTcrid = new HashMap<>();

        // 7. For each active batch, create semester and courses
        for (Batches batch : activeBatches) {
            Programs program = programsRepository.findById(batch.getBchprgid()).orElse(null);
            if (program == null) continue;

            // Get batch's academic year
            Long batchAyrId = batch.getBchcalid();
            AcademicYears batchYear = academicYearRepository.findById(batchAyrId)
                    .orElseThrow(() -> new RuntimeException("Batch Year not found"));

            String batchAyrName = batchYear.getAyrname();
            int batchYearStarting = Integer.parseInt(batchAyrName.split("-")[0]);

            // Calculate year difference (program year = diff + 1)
            int diff = academicYearStarting - batchYearStarting;
            long programYear = diff + 1;

            // Skip Summer for last year
            if ("Summer".equalsIgnoreCase(termName) && diff == program.getPrgduration() - 1) {
                continue;
            }

            // Calculate semester name based on term type
            String strName;
            if ("Summer".equalsIgnoreCase(termName)) {
                // Summer I, Summer II, etc. based on diff
                strName = "Summer " + RomanNumeralUtil.toRoman(diff + 1);
            } else {
                // Regular semester: Autumn = odd, Winter = even
                // Semester number = diff * 2 + (Autumn=1, Winter=2)
                long semNo = diff * 2L + ("Autumn".equalsIgnoreCase(termName) ? 1 : 2);
                strName = "Semester " + RomanNumeralUtil.toRoman(semNo);
            }

            // Get max strseqno for this batch and increment
            Long maxSeqNo = semesterRepository.findMaxSemesterSeqNo(batch.getBchid());
            Long newSeqNo = (maxSeqNo == null ? 0 : maxSeqNo) + 1;

            // Generate new semester ID
            Long maxSemId = semesterRepository.findMaxSemesterId();
            Long newSemId = (maxSemId == null ? 0 : maxSemId) + 1;

            // Create Semester
            Semesters semester = new Semesters();
            semester.setBatches(batch);
            semester.setTerms(term);
            semester.setStrid(newSemId);
            semester.setStrbchid(batch.getBchid());
            semester.setStrtrmid(newTermId);
            semester.setStrname(strName);
            semester.setStrseqno(newSeqNo);
            semester.setStrcalid(academicYearId);
            semesterRepository.save(semester);

            // 8. Get scheme courses for this batch's scheme, splids, term, and program year
            Long schemeId = batch.getSchemeId();
            Long splid = batch.getSplid();
        //     Long splid = batch.getSplid() != null ? batch.getSplid() : 0L;
            
            // Get splids: if splid > 0, include both 0 and splid; otherwise just 0
            List<Long> splids = splid > 0L ? Arrays.asList(0L, splid) : List.of(0L);
            
            // Get scheme courses ordered by splid then courseSrNo
            List<SchemeCourses> schemeCourses = schemeCoursesRepository
                    .findBySchemeIdAndSplidInAndTermNameAndProgramYearOrderBySplidAscCourseSrNoAsc(
                            schemeId, splids, termName, programYear);

            // 9. Process scheme courses - populate TermCourses and SemesterCourses
            long scrSeqNo = 1;
            for (SchemeCourses sc : schemeCourses) {
                Long crsid = sc.getCrsid();
                Long tcrid = null;
                String crstype;

                if (crsid != null) {
                    Courses course = coursesRepository.findById(crsid).orElse(null);
                    // Course with crsid - add to TermCourses if not already added
                    if (crsidToTcrid.containsKey(crsid)) {
                        // Already added to TermCourses, reuse tcrid
                        tcrid = crsidToTcrid.get(crsid);
                        // Get crstype from TermCourses
                        TermCourses existingTc = termCourseRepository.findByTcrid(tcrid);
                        crstype = existingTc != null ? existingTc.getCrstype() : course.getCrstype();
                    } else {
                        // Check if this course already exists in TermCourses for this term
                        boolean exists = termCourseRepository.existsByTcrtrmidAndTcrcrsid(newTermId, crsid);
                        if (exists) {
                            // Find existing tcrid
                            tcrid = termCourseRepository.findTcridByCrsidAndTrmid(crsid, newTermId);
                            crsidToTcrid.put(crsid, tcrid);
                            TermCourses existingTc = termCourseRepository.findByTcrid(tcrid);
                            crstype = existingTc != null ? existingTc.getCrstype() : course.getCrstype();
                        } else {
                            // Add new entry to TermCourses
                            Long maxTcrId = termCourseRepository.findMaxTcrid();
                            Long newTcrId = (maxTcrId == null ? 0 : maxTcrId) + 1;

                            // Get crstype from crstype in Courses
                            crstype = course.getCrstype();

                            TermCourses tc = new TermCourses();
                            tc.setTcrid(newTcrId);
                            tc.setTcrtrmid(newTermId);
                            tc.setTcrcrsid(crsid);
                            tc.setTcrtype("REGULAR");
                            tc.setTcrrowstate(1L);
                            tc.setTcrstatus("AVAILABLE");
                            tc.setCrstype(crstype);
                            tc.setTcrcreatedat(LocalDateTime.now());
                            termCourseRepository.save(tc);

                            tcrid = newTcrId;
                            crsidToTcrid.put(crsid, tcrid);
                        }
                    }
                } else {
                    // Course without crsid (elective placeholder) - no TermCourse entry
                //     tcrid = null;
                //     crstype = "ELECTIVE";
                    continue;   // skip adding to SemesterCourses due to insufficient attributes to make a meaningful entry
                }

                // 10. Add to SemesterCourses
                Long maxScrId = semesterCoursesRepository.findMaxSemesterCourseid();
                Long newScrId = (maxScrId == null ? 0 : maxScrId) + 1;

                SemesterCourses semCourse = new SemesterCourses();
                semCourse.setScrid(newScrId);
                semCourse.setScrstrid(newSemId);
                semCourse.setScrcrsid(crsid);
                semCourse.setScrtcrid(tcrid);
                semCourse.setCrstype(crstype);
                semCourse.setScrseqno(scrSeqNo);
                semCourse.setScrrowstate(1L);
                semCourse.setScrcreatedat(LocalDateTime.now());
                semesterCoursesRepository.save(semCourse);

                scrSeqNo++;
            }
        }

        return "redirect:/admin/terms";
    }

    // LIST TERMS (DESC ORDER)
    @GetMapping
    public String listTerms(Model model) {
        List<Object[]> rows = termRepository.getAllTermsDetailsRaw();

        List<TermViewDTO> terms = rows.stream()
                .map(r -> new TermViewDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (Date) r[3],
                        (Date) r[4]
                )).toList();

        Map<String, List<TermViewDTO>> termsByAcademicYear =
                terms.stream()
                        .collect(Collectors.groupingBy(
                                TermViewDTO::ayrname,
                                LinkedHashMap::new, // preserves the order
                                Collectors.toList()
                        ));

        model.addAttribute("termsByAcademicYear", termsByAcademicYear);
        return "admin/terms";
    }

}

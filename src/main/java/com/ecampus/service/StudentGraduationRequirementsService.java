package com.ecampus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecampus.dto.CourseConversionCourseDTO;
import com.ecampus.dto.CourseConversionTypeGroupDTO;
import com.ecampus.dto.CourseTypeProgressDTO;
import com.ecampus.dto.CourseTypeOptionDTO;
import com.ecampus.dto.OverallCourseTypeProgressDTO;
import com.ecampus.dto.StudentGraduationRequirementsAdminDTO;
import com.ecampus.model.*;
import com.ecampus.repository.*;

@Service
public class StudentGraduationRequirementsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StudentsRepository studentsRepo;

    @Autowired
    private BatchesRepository batchesRepo;

    @Autowired
    private SemestersRepository semestersRepo;

    @Autowired
    private CourseTypesRepository courseTypesRepo;

    @Autowired
    private SchemeCoursesRepository schemeCoursesRepo;

    @Autowired
    private StudentRegistrationsRepository studentRegistrationsRepo;

    @Autowired
    private StudentRegistrationCoursesRepository studentRegCoursesRepo;

    @Autowired
    private Egcrstt1Repository egcrstt1Repo;

    @Autowired
    private TermCoursesRepository termCoursesRepo;

    @Autowired
    private CoursesRepository coursesRepo;

    @Autowired
    private CourseTypeConversionRepository courseTypeConversionRepo;

    @Autowired
    private SchemeDetailsRepository schemeDetailsRepo;

    @Autowired
    private StudentSemesterResultRepository studentSemesterResultRepo;

    /**
     * Returns the stdid for the logged-in user.
     */
    public Long getStudentIdByUsername(String username) {
        return userRepo.findIdByUname(username);
    }

    /**
     * Returns the Students entity.
     */
    public Students getStudent(Long stdid) {
        return studentsRepo.findStudent(stdid);
    }

    /**
     * Returns the Batches entity for a student.
     */
    public Batches getBatch(Long batchId) {
        return batchesRepo.findById(batchId).orElse(null);
    }

    /**
     * Returns the current (latest) semester name for a batch.
     */
    public String getCurrentSemesterName(Long batchId) {
        Long maxStrid = semestersRepo.findMaxSemesterId(batchId);
        if (maxStrid == null) return null;
        return semestersRepo.findById(maxStrid).map(Semesters::getStrname).orElse(null);
    }

    /**
     * Builds the course-type-wise progress for a student until the current semester.
     */
    public List<CourseTypeProgressDTO> buildCurrentSemesterProgress(Long stdid, Long batchId,
                                                                      Long schemeId, Long splid,
                                                                      String currentSemesterName) {
        // Get applicable splids: if splid==0 use [0], else use [0, splid]
        List<Long> applicableSplids = getApplicableSplids(splid);

        // --- a. Get all course types for this scheme + applicable splids ---
        List<CourseTypes> courseTypes = courseTypesRepo.findBySchemeIdAndSplidIn(schemeId, applicableSplids);

        // --- b. Generate semester name list up to current ---
        List<String> semesterNames = generateSemesterNamesUpTo(currentSemesterName);

        // --- b. Count required courses per ctpid from SchemeCourses ---
        Map<Long, Long> requiredByCtpid = new HashMap<>();
        if (!semesterNames.isEmpty()) {
            List<SchemeCourses> schemeCourses = schemeCoursesRepo
                    .findBySchemeIdAndSplidInAndSemesterNameIn(schemeId, applicableSplids, semesterNames);
            requiredByCtpid = schemeCourses.stream()
                    .filter(sc -> sc.getCtpid() != null)
                    .collect(Collectors.groupingBy(SchemeCourses::getCtpid, Collectors.counting()));
        }

        // --- c. Count completed (passed) courses per ctpid ---
        CompletedCourseMetrics completedMetrics = buildCompletedCourseMetrics(stdid);
        Map<Long, Long> completedByCtpid = completedMetrics.completedCounts();

        // --- Build progress list ---
        List<CourseTypeProgressDTO> progressList = new ArrayList<>();
        for (CourseTypes ct : courseTypes) {
            long required = requiredByCtpid.getOrDefault(ct.getCtpid(), 0L);
            long completed = completedByCtpid.getOrDefault(ct.getCtpid(), 0L);
            progressList.add(new CourseTypeProgressDTO(
                    ct.getCtpid(), ct.getCtpcode(), ct.getCtpname(), ct.getCrscat(),
                    required, completed));
        }

        // Also include any ctpid where student completed courses but is not in scheme course types
        Set<Long> knownCtpids = courseTypes.stream().map(CourseTypes::getCtpid).collect(Collectors.toSet());
        for (Map.Entry<Long, Long> entry : completedByCtpid.entrySet()) {
            if (!knownCtpids.contains(entry.getKey())) {
                CourseTypes extra = courseTypesRepo.findById(entry.getKey()).orElse(null);
                if (extra != null) {
                    long required = requiredByCtpid.getOrDefault(entry.getKey(), 0L);
                    progressList.add(new CourseTypeProgressDTO(
                            extra.getCtpid(), extra.getCtpcode(), extra.getCtpname(), extra.getCrscat(),
                            required, entry.getValue()));
                }
            }
        }

        return progressList;
    }

    /**
     * Builds the course-type-wise overall graduation progress for a student.
     */
    public List<OverallCourseTypeProgressDTO> buildOverallProgress(Long stdid, Long schemeId, Long splid) {
        // Get applicable splids: if splid==0 use [0], else use [0, splid]
        List<Long> applicableSplids = getApplicableSplids(splid);

        List<CourseTypes> courseTypes = courseTypesRepo.findBySchemeIdAndSplidIn(schemeId, applicableSplids);
        CompletedCourseMetrics completedMetrics = buildCompletedCourseMetrics(stdid);

        List<OverallCourseTypeProgressDTO> progressList = new ArrayList<>();
        for (CourseTypes ct : courseTypes) {
            progressList.add(new OverallCourseTypeProgressDTO(
                    ct.getCtpid(),
                    ct.getCtpcode(),
                    ct.getCtpname(),
                    ct.getCrscat(),
                    safeLong(ct.getMinCourses()),
                    completedMetrics.completedCounts().getOrDefault(ct.getCtpid(), 0L),
                    BigDecimal.valueOf(safeLong(ct.getMinCredits())),
                    completedMetrics.completedCredits().getOrDefault(ct.getCtpid(), BigDecimal.ZERO)));
        }

        Set<Long> knownCtpids = courseTypes.stream().map(CourseTypes::getCtpid).collect(Collectors.toSet());
        for (Map.Entry<Long, Long> entry : completedMetrics.completedCounts().entrySet()) {
            if (!knownCtpids.contains(entry.getKey())) {
                CourseTypes extra = courseTypesRepo.findById(entry.getKey()).orElse(null);
                if (extra != null) {
                    progressList.add(new OverallCourseTypeProgressDTO(
                            extra.getCtpid(),
                            extra.getCtpcode(),
                            extra.getCtpname(),
                            extra.getCrscat(),
                            0L,
                            entry.getValue(),
                            BigDecimal.ZERO,
                            completedMetrics.completedCredits().getOrDefault(entry.getKey(), BigDecimal.ZERO)));
                }
            }
        }

        return progressList;
    }

    /**
     * Builds course-type-wise list of all eligible student courses for conversion UI.
     */
    public List<CourseConversionTypeGroupDTO> buildCourseConversionGroups(Long stdid, Long schemeId, Long splid) {
        return buildCourseConversionComputation(stdid, schemeId, splid).groups();
    }

    /**
     * Updates curr_ctpid for a single student registration course after validating conversion eligibility.
     */
    @Transactional
    public void updateCourseTypeConversion(Long stdid, Long schemeId, Long splid, Long srcid, Long newCtpid) {
        CourseConversionComputation computation = buildCourseConversionComputation(stdid, schemeId, splid);
        CourseConversionComputedCourse target = computation.courseBySrcId().get(srcid);
        if (target == null) {
            throw new IllegalArgumentException("Course record is not available for conversion.");
        }
        if (!target.dropdownEnabled()) {
            throw new IllegalArgumentException("This course is not eligible for course-type conversion.");
        }
        if (!target.optionCtpidSet().contains(newCtpid)) {
            throw new IllegalArgumentException("Selected course type is not allowed for this course.");
        }

        StudentRegistrationCourses src = studentRegCoursesRepo.findBySrcid(srcid)
                .orElseThrow(() -> new IllegalArgumentException("Student course record not found."));

        Set<Long> studentSrgIds = studentRegistrationsRepo.findregisteredsemesters(stdid).stream()
                .map(StudentRegistrations::getSrgid)
                .collect(Collectors.toSet());
        if (!studentSrgIds.contains(src.getSrcsrgid())) {
            throw new IllegalArgumentException("You are not allowed to modify this course record.");
        }

        src.setCurrCtpid(newCtpid);
        studentRegCoursesRepo.save(src);
    }

    private CourseConversionComputation buildCourseConversionComputation(Long stdid, Long schemeId, Long splid) {
        // Get applicable splids: if splid==0 use [0], else use [0, splid]
        List<Long> applicableSplids = getApplicableSplids(splid);

        List<CourseTypes> courseTypes = courseTypesRepo.findBySchemeIdAndSplidIn(schemeId, applicableSplids);
        Map<Long, CourseTypes> courseTypeById = courseTypes.stream()
                .collect(Collectors.toMap(CourseTypes::getCtpid, ct -> ct, (left, right) -> left));
        Map<Long, Long> minCoursesByCtpid = courseTypes.stream()
                .collect(Collectors.toMap(CourseTypes::getCtpid, ct -> safeLong(ct.getMinCourses()), (left, right) -> left));

        List<StudentRegistrations> registrations = studentRegistrationsRepo.findregisteredsemesters(stdid);
        if (registrations.isEmpty()) {
            return new CourseConversionComputation(Collections.emptyList(), Collections.emptyMap());
        }

        List<Long> srgIds = registrations.stream().map(StudentRegistrations::getSrgid).toList();
        List<StudentRegistrationCourses> regCourses = studentRegCoursesRepo.findBySrcsrgidIn(srgIds);
        if (regCourses.isEmpty()) {
            return new CourseConversionComputation(Collections.emptyList(), Collections.emptyMap());
        }

        List<Long> tcrIds = regCourses.stream()
                .map(StudentRegistrationCourses::getSrctcrid)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (tcrIds.isEmpty()) {
            return new CourseConversionComputation(Collections.emptyList(), Collections.emptyMap());
        }

        Map<Long, List<Egcrstt1>> gradesByTcr = egcrstt1Repo.findByStudIdAndTcridIn(stdid, tcrIds).stream()
                .collect(Collectors.groupingBy(Egcrstt1::getTcrid));

        Set<Long> includedTcrIds = tcrIds.stream()
                .filter(tcrid -> isIncludedForConversion(gradesByTcr.getOrDefault(tcrid, Collections.emptyList())))
                .collect(Collectors.toSet());
        if (includedTcrIds.isEmpty()) {
            return new CourseConversionComputation(Collections.emptyList(), Collections.emptyMap());
        }

        Map<Long, TermCourses> termCourseById = termCoursesRepo.findAllById(includedTcrIds).stream()
                .collect(Collectors.toMap(TermCourses::getTcrid, tc -> tc, (left, right) -> left));
        Set<Long> courseIds = termCourseById.values().stream()
                .map(TermCourses::getTcrcrsid)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Courses> courseById = coursesRepo.findAllById(courseIds).stream()
                .collect(Collectors.toMap(Courses::getCrsid, c -> c, (left, right) -> left));

        List<CourseTypeConversion> conversionRules = courseTypeConversionRepo.findAll();
        Map<Long, LinkedHashSet<Long>> allowedByOrigCtpid = new HashMap<>();
        for (CourseTypeConversion rule : conversionRules) {
            if (rule.getOrigCtpid() == null || rule.getAllowedCtpid() == null) {
                continue;
            }
            allowedByOrigCtpid
                    .computeIfAbsent(rule.getOrigCtpid(), key -> new LinkedHashSet<>())
                    .add(rule.getAllowedCtpid());
        }

        List<WorkingCourse> workingCourses = new ArrayList<>();
        for (StudentRegistrationCourses src : regCourses) {
            Long tcrid = src.getSrctcrid();
            if (tcrid == null || !includedTcrIds.contains(tcrid)) {
                continue;
            }

            Long origCtpid = src.getOrigCtpid();
            Long currCtpid = src.getCurrCtpid() != null ? src.getCurrCtpid() : origCtpid;
            if (currCtpid == null) {
                continue;
            }

            TermCourses termCourse = termCourseById.get(tcrid);
            if (termCourse == null || termCourse.getTcrcrsid() == null) {
                continue;
            }

            Courses course = courseById.get(termCourse.getTcrcrsid());
            if (course == null) {
                continue;
            }

            workingCourses.add(new WorkingCourse(src, termCourse, course, origCtpid, currCtpid));
        }

        Map<Long, Long> totalByCurrentCtpid = workingCourses.stream()
                .collect(Collectors.groupingBy(WorkingCourse::currentCtpid, Collectors.counting()));

        Map<Long, CourseConversionTypeGroupDTO> groupsByCtpid = new LinkedHashMap<>();
        for (CourseTypes ct : courseTypes) {
            CourseConversionTypeGroupDTO group = new CourseConversionTypeGroupDTO();
            group.setCtpid(ct.getCtpid());
            group.setCtpcode(ct.getCtpcode());
            group.setCtpname(ct.getCtpname());
            group.setCrscat(ct.getCrscat());
            group.setMinCourses(safeLong(ct.getMinCourses()));
            group.setTotalCourses(totalByCurrentCtpid.getOrDefault(ct.getCtpid(), 0L));
            groupsByCtpid.put(ct.getCtpid(), group);
        }

        Map<Long, CourseConversionComputedCourse> computedBySrcId = new HashMap<>();
        for (WorkingCourse wc : workingCourses) {
            CourseConversionTypeGroupDTO group = groupsByCtpid.computeIfAbsent(wc.currentCtpid(), key -> {
                CourseConversionTypeGroupDTO dynamic = new CourseConversionTypeGroupDTO();
                dynamic.setCtpid(key);
                CourseTypes extraType = courseTypesRepo.findById(key).orElse(null);
                if (extraType != null) {
                    dynamic.setCtpcode(extraType.getCtpcode());
                    dynamic.setCtpname(extraType.getCtpname());
                    dynamic.setCrscat(extraType.getCrscat());
                } else {
                    dynamic.setCtpcode("CTP-" + key);
                    dynamic.setCtpname("Unknown Course Type");
                    dynamic.setCrscat("OTHER");
                }
                dynamic.setMinCourses(minCoursesByCtpid.getOrDefault(key, 0L));
                dynamic.setTotalCourses(totalByCurrentCtpid.getOrDefault(key, 0L));
                return dynamic;
            });

            long minRequired = group.getMinCourses();
            long currentTotal = totalByCurrentCtpid.getOrDefault(wc.currentCtpid(), 0L);
            boolean exceedsMin = currentTotal > minRequired;
            boolean alreadyChanged = wc.origCtpid() != null && !Objects.equals(wc.origCtpid(), wc.currentCtpid());
            boolean hasConversionRule = wc.origCtpid() != null && allowedByOrigCtpid.containsKey(wc.origCtpid());

            LinkedHashSet<Long> optionCtpids = new LinkedHashSet<>();
            if (wc.origCtpid() != null) {
                optionCtpids.add(wc.origCtpid());
            }
            if (wc.origCtpid() != null) {
                optionCtpids.addAll(allowedByOrigCtpid.getOrDefault(wc.origCtpid(), new LinkedHashSet<>()));
            }
            optionCtpids.add(wc.currentCtpid());

            boolean dropdownEnabled = alreadyChanged || (exceedsMin && hasConversionRule);

            List<CourseTypeOptionDTO> optionDtos = new ArrayList<>();
            for (Long optionCtpid : optionCtpids) {
                if (optionCtpid == null) {
                    continue;
                }
                CourseTypes optionType = courseTypeById.get(optionCtpid);
                if (optionType == null) {
                    optionType = courseTypesRepo.findById(optionCtpid).orElse(null);
                    if (optionType != null) {
                        courseTypeById.put(optionCtpid, optionType);
                    }
                }
                String optionCode = optionType != null && optionType.getCtpcode() != null
                        ? optionType.getCtpcode()
                        : ("CTP-" + optionCtpid);
                optionDtos.add(new CourseTypeOptionDTO(optionCtpid, optionCode));
            }

            CourseTypes currType = courseTypeById.get(wc.currentCtpid());
            CourseTypes origType = wc.origCtpid() != null ? courseTypeById.get(wc.origCtpid()) : null;

            CourseConversionCourseDTO courseDto = new CourseConversionCourseDTO();
            courseDto.setSrcid(wc.src().getSrcid());
            courseDto.setTcrid(wc.termCourse().getTcrid());
            courseDto.setOrigCtpid(wc.origCtpid());
            courseDto.setCurrCtpid(wc.currentCtpid());
            courseDto.setOrigCtpcode(origType != null ? origType.getCtpcode() : null);
            courseDto.setCurrCtpcode(currType != null ? currType.getCtpcode() : ("CTP-" + wc.currentCtpid()));
            courseDto.setCrscode(wc.course().getCrscode());
            courseDto.setCrsname(wc.course().getCrsname());
            courseDto.setCreditHours(formatCreditHours(wc.course()));
            courseDto.setDropdownEnabled(dropdownEnabled);
            courseDto.setChangedFromOriginal(alreadyChanged);
            courseDto.setOptions(optionDtos);

            group.getCourses().add(courseDto);
            computedBySrcId.put(wc.src().getSrcid(),
                    new CourseConversionComputedCourse(courseDto.isDropdownEnabled(), optionCtpids));
        }

        List<CourseConversionTypeGroupDTO> groups = groupsByCtpid.values().stream()
                .peek(group -> group.getCourses().sort(Comparator
                        .comparing((CourseConversionCourseDTO c) -> nullSafe(c.getCrscode()))
                        .thenComparing(c -> nullSafe(c.getCrsname()))))
                .collect(Collectors.toList());

        return new CourseConversionComputation(groups, computedBySrcId);
    }

    /**
     * Builds the map of ctpid -> count of completed/passed courses for a student.
     */
    private CompletedCourseMetrics buildCompletedCourseMetrics(Long stdid) {
        // Get all student registrations
        List<StudentRegistrations> registrations = studentRegistrationsRepo.findregisteredsemesters(stdid);
        if (registrations.isEmpty()) {
            return CompletedCourseMetrics.empty();
        }

        List<Long> srgIds = registrations.stream()
                .map(StudentRegistrations::getSrgid)
                .toList();

        // Get all registration courses
        List<StudentRegistrationCourses> regCourses = studentRegCoursesRepo.findBySrcsrgidIn(srgIds);
        if (regCourses.isEmpty()) {
            return CompletedCourseMetrics.empty();
        }

        // Map tcrid -> curr_ctpid
        Map<Long, Long> tcrToCtpMap = new HashMap<>();
        for (StudentRegistrationCourses rc : regCourses) {
            Long ctpid = rc.getCurrCtpid() != null ? rc.getCurrCtpid() : rc.getOrigCtpid();
            if (ctpid != null) {
                tcrToCtpMap.put(rc.getSrctcrid(), ctpid);
            }
        }

        List<Long> tcrIds = new ArrayList<>(tcrToCtpMap.keySet());
        if (tcrIds.isEmpty()) {
            return CompletedCourseMetrics.empty();
        }

        // Get grade entries
        List<Egcrstt1> grades = egcrstt1Repo.findByStudIdAndTcridIn(stdid, tcrIds);

        // Filter: only passed (entry exists AND obtgr_id not 5 and not 8)
        Set<Long> passedTcrIds = grades.stream()
                .filter(g -> g.getObtgrId() != null && g.getObtgrId() != 5L && g.getObtgrId() != 8L)
                .map(Egcrstt1::getTcrid)
                .collect(Collectors.toSet());

        if (passedTcrIds.isEmpty()) {
            return CompletedCourseMetrics.empty();
        }

        Map<Long, Long> tcrToCourseId = termCoursesRepo.findAllById(passedTcrIds).stream()
            .filter(termCourse -> termCourse.getTcrcrsid() != null)
            .collect(Collectors.toMap(TermCourses::getTcrid, TermCourses::getTcrcrsid));

        Set<Long> courseIds = new HashSet<>(tcrToCourseId.values());
        Map<Long, BigDecimal> creditsByCourseId = coursesRepo.findAllById(courseIds).stream()
            .collect(Collectors.toMap(Courses::getCrsid,
                course -> course.getCrscreditpoints() != null ? course.getCrscreditpoints() : BigDecimal.ZERO));

        // Count by ctpid
        Map<Long, Long> completedByCtpid = new HashMap<>();
        Map<Long, BigDecimal> completedCreditsByCtpid = new HashMap<>();
        for (Long tcrid : passedTcrIds) {
            Long ctpid = tcrToCtpMap.get(tcrid);
            if (ctpid != null) {
                completedByCtpid.merge(ctpid, 1L, Long::sum);
            Long courseId = tcrToCourseId.get(tcrid);
            BigDecimal credits = courseId != null
                ? creditsByCourseId.getOrDefault(courseId, BigDecimal.ZERO)
                : BigDecimal.ZERO;
            completedCreditsByCtpid.merge(ctpid, credits, BigDecimal::add);
            }
        }
        return new CompletedCourseMetrics(completedByCtpid, completedCreditsByCtpid);
    }

    /**
     * Generates the ordered list of semester names up to and including the given semester.
     * Pattern: Semester I, Semester II, Summer I, Semester III, Semester IV, Summer II, ...
     */
    private List<String> generateSemesterNamesUpTo(String targetName) {
        if (targetName == null || targetName.isBlank()) return Collections.emptyList();

        List<String> result = new ArrayList<>();
        for (int group = 1; group <= 10; group++) {
            String sem1 = "Semester " + toRoman(2 * group - 1);
            result.add(sem1);
            if (sem1.equalsIgnoreCase(targetName)) return result;

            String sem2 = "Semester " + toRoman(2 * group);
            result.add(sem2);
            if (sem2.equalsIgnoreCase(targetName)) return result;

            String summer = "Summer " + toRoman(group);
            result.add(summer);
            if (summer.equalsIgnoreCase(targetName)) return result;
        }

        // If target was not matched by the pattern, return whatever we generated
        // This handles edge cases where naming might differ
        return result;
    }

    private static String toRoman(int num) {
        String[] r = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
                       "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX"};
        if (num >= 1 && num < r.length) return r[num];
        return String.valueOf(num);
    }

    private boolean isIncludedForConversion(List<Egcrstt1> grades) {
        if (grades == null || grades.isEmpty()) {
            return true;
        }
        return grades.stream().anyMatch(grade -> grade.getObtgrId() == null ||
                (grade.getObtgrId() != 5L && grade.getObtgrId() != 8L));
    }

    private String formatCreditHours(Courses course) {
        BigDecimal points = safeDecimal(course.getCrscreditpoints());
        BigDecimal lectures = safeDecimal(course.getCrslectures());
        BigDecimal tutorials = safeDecimal(course.getCrstutorials());
        BigDecimal practicals = safeDecimal(course.getCrspracticals());
        return formatDecimal(points) + " (" + formatDecimal(lectures) + " + " +
                formatDecimal(tutorials) + " + " + formatDecimal(practicals) + ")";
    }

    private BigDecimal safeDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatDecimal(BigDecimal value) {
        return safeDecimal(value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    /**
     * Returns the applicable splids based on the given splid:
     * - If splid == 0, returns [0]
     * - If splid > 0, returns [0, splid]
     * This is because a specialization inherits all base courses from splid=0.
     */
    private List<Long> getApplicableSplids(Long splid) {
        if (splid == null || splid <= 0) {
            return Arrays.asList(0L);
        }
        return Arrays.asList(0L, splid);
    }

    private long safeLong(Long value) {
        return value != null ? value : 0L;
    }

    private record WorkingCourse(StudentRegistrationCourses src,
                                 TermCourses termCourse,
                                 Courses course,
                                 Long origCtpid,
                                 Long currentCtpid) {
    }

    private record CourseConversionComputedCourse(boolean dropdownEnabled, Set<Long> optionCtpidSet) {
    }

    private record CourseConversionComputation(List<CourseConversionTypeGroupDTO> groups,
                                               Map<Long, CourseConversionComputedCourse> courseBySrcId) {
    }

    private record CompletedCourseMetrics(Map<Long, Long> completedCounts,
                                          Map<Long, BigDecimal> completedCredits) {
        private static CompletedCourseMetrics empty() {
            return new CompletedCourseMetrics(Collections.emptyMap(), Collections.emptyMap());
        }
    }

    /**
     * Builds graduation requirements summary for all students in a batch.
     * Returns a list of StudentGraduationRequirementsAdminDTO sorted by stdinstid.
     */
    public List<StudentGraduationRequirementsAdminDTO> buildBatchGraduationRequirements(
            Long batchId, Long schemeId, Long splid) {
        
        // 1. Get all students in the batch
        Batches batch = batchesRepo.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));
        
        List<Students> students = studentsRepo.findByBatch(batch).stream()
                .filter(s -> s.getStdrowstate() > 0)
                .sorted((s1, s2) -> {
                    String id1 = s1.getStdinstid() != null ? s1.getStdinstid() : "";
                    String id2 = s2.getStdinstid() != null ? s2.getStdinstid() : "";
                    return id1.compareTo(id2);
                })
                .toList();
        
        // 2. Get scheme details for min CPI and other requirements
        SchemeDetails schemeDetails = null;
        if (splid != null && splid > 0) {
            schemeDetails = schemeDetailsRepo.findBySchemeIdAndSplid(schemeId, splid).orElse(null);
        }
        if (schemeDetails == null) {
            schemeDetails = schemeDetailsRepo.findBySchemeIdAndSplid(schemeId, 0L).orElse(null);
        }
        
        BigDecimal minCpi = schemeDetails != null && schemeDetails.getSplMinCpi() != null
                ? schemeDetails.getSplMinCpi()
                : BigDecimal.ZERO;
        
        // 3. Get all course types for total types count
        List<Long> applicableSplids = getApplicableSplids(splid);
        List<CourseTypes> courseTypes = courseTypesRepo.findBySchemeIdAndSplidIn(schemeId, applicableSplids);
        long totalTypes = courseTypes.size();
        
        // Calculate total min courses and credits across all types
        long totalMinCourses = courseTypes.stream()
                .mapToLong(ct -> safeLong(ct.getMinCourses()))
                .sum();
        BigDecimal totalMinCredits = courseTypes.stream()
                .map(ct -> BigDecimal.valueOf(safeLong(ct.getMinCredits())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 4. Build DTO for each student
        List<StudentGraduationRequirementsAdminDTO> results = new ArrayList<>();
        
        for (Students student : students) {
            // Get overall progress for this student
            List<OverallCourseTypeProgressDTO> progressList = buildOverallProgress(
                    student.getStdid(), schemeId, splid);
            
            // Aggregate totals
            long totalCompleted = progressList.stream()
                    .mapToLong(OverallCourseTypeProgressDTO::getCompletedCourses)
                    .sum();
            long totalExtra = progressList.stream()
                    .mapToLong(OverallCourseTypeProgressDTO::getExtraCourses)
                    .sum();
            long coursesCompleted = totalCompleted - totalExtra;
            
            BigDecimal creditsEarned = progressList.stream()
                    .map(OverallCourseTypeProgressDTO::getCompletedCredits)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal extraCredits = progressList.stream()
                    .map(OverallCourseTypeProgressDTO::getExtraCredits)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            creditsEarned = creditsEarned.subtract(extraCredits);
            // Normalize scale (remove trailing zeros)
            creditsEarned = creditsEarned.stripTrailingZeros();
            
            // Count types fulfilled
            long typesFulfilled = progressList.stream()
                    .filter(p -> p.isCourseRequirementMet() && p.isCreditRequirementMet())
                    .count();
            
            // Get student's CPI
            // Get student's CPI (still coming as String from DB)
            String rawCpi = studentSemesterResultRepo.getcpi(student.getStdid());

            // Convert to BigDecimal (null if invalid)
            BigDecimal studentCpi = parseCpi(rawCpi);
            
            // Determine graduation status
            boolean courseRequirementMet = coursesCompleted >= totalMinCourses;
            boolean creditRequirementMet = creditsEarned.compareTo(totalMinCredits) >= 0;
            boolean typesRequirementMet = typesFulfilled >= totalTypes;
            boolean cpiRequirementMet = studentCpi.compareTo(minCpi) >= 0;
            
            boolean isCompleted = courseRequirementMet && creditRequirementMet && 
                                 typesRequirementMet && cpiRequirementMet;
            
            String status = isCompleted ? "Completed" : "Incomplete";
            String statusColor = isCompleted ? "success" : "warning";
            
            // Build student name
            String studentName = buildStudentFullName(student);
            
            results.add(new com.ecampus.dto.StudentGraduationRequirementsAdminDTO(
                    student.getStdid(),
                    student.getStdinstid(),
                    studentName,
                    coursesCompleted,
                    totalMinCourses,
                    creditsEarned,
                    totalMinCredits,
                    typesFulfilled,
                    totalTypes,
                    studentCpi,
                    minCpi,
                    status,
                    statusColor
            ));
        }
        
        return results;
    }

    /**
     * Builds full student name from available fields: firstname [middlename] lastname
     */
    private String buildStudentFullName(Students student) {
        StringBuilder name = new StringBuilder();
        if (student.getStdfirstname() != null && !student.getStdfirstname().isBlank()) {
            name.append(student.getStdfirstname());
        }
        if (student.getStdmiddlename() != null && !student.getStdmiddlename().isBlank()) {
            if (name.length() > 0) name.append(" ");
            name.append(student.getStdmiddlename());
        }
        if (student.getStdlastname() != null && !student.getStdlastname().isBlank()) {
            if (name.length() > 0) name.append(" ");
            name.append(student.getStdlastname());
        }
        return name.toString();
    }

    /**
     * Parses CPI safely from DB value.
     * Returns null if CPI is invalid, missing, or non-numeric.
     */
    private BigDecimal parseCpi(String cpiStr) {
        if (cpiStr == null) {
            return new BigDecimal(0);
        }

        cpiStr = cpiStr.trim();

        if (cpiStr.isEmpty() || cpiStr.equals("--")) {
            return new BigDecimal(0);
        }

        if (!cpiStr.matches("\\d+(\\.\\d+)?")) {
            return new BigDecimal(0);
        }

        // Normalize comma decimal (if your DB might contain it)
        cpiStr = cpiStr.replace(",", ".");

        try {
            return new BigDecimal(cpiStr);
        } catch (NumberFormatException e) {
            return new BigDecimal(0);
        }
    }
}

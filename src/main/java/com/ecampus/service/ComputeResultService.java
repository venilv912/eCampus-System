package com.ecampus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecampus.model.*;
import com.ecampus.repository.*;

@Service
public class ComputeResultService {

    @Autowired
    private StudentRegistrationsRepository stdRegRepo;

    @Autowired
    private SemestersRepository semRepo;

    @Autowired
    private Egcrstt1Repository egcrstt1Repo;

    @Autowired
    private LieuCoursesRepository lieuCrsRepo;

    @Autowired
    private StudentRegistrationCoursesRepository stdRegCrsRepo;

    @Autowired
    private StudentSemesterResultRepository ssrRepo;

    @Transactional
    public void computeResult(Long strid) {

        List<StudentRegistrations> stdRegs = stdRegRepo.findByStrid(strid);
        Long ssrid = ssrRepo.findMaxId();
        ssrid+=1;
        for(StudentRegistrations srg : stdRegs){

            Map<String, Object> spi=computeSPI(strid, srg.getSrgstdid(), srg.getSrgid());
            List<Long> eligibleCourseIds = computeCourses(strid,srg.getSrgstdid(),srg.getSrgid());
            Map<String, Object> cpi=computeCPI(srg.getSrgstdid(), eligibleCourseIds);

            System.out.println("Final spiCreditsRegistered: " + toBigDecimal(spi.get("creditsRegistered")));
            System.out.println("Final spiCreditsEarned: " + toBigDecimal(spi.get("creditsEarned")));
            System.out.println("Final spiGradePoints: " + toBigDecimal(spi.get("gradePointsEarned")));
            System.out.println("Final spiGradePoints: " + toBigDecimal(spi.get("spiGradePoints")));
            System.out.println("Final spiRegisteredCredits: " + toBigDecimal(spi.get("spiRegisteredCredits")));
            System.out.println("Computed SPI for Semester: " + spi.get("semesterSPI"));

            System.out.println("Final totalCpiCreditsRegistered CPI: " + toBigDecimal(cpi.get("cpicreditsRegistered")));
            System.out.println("Final totalCpiCreditsEarned CPI: " + toBigDecimal(cpi.get("cpicreditsEarned")));
            System.out.println("Final totalCpiGradePoints CPI: " + toBigDecimal(cpi.get("cpigradePointsEarned")));
            System.out.println("Final totalCpiGradePoints CPI: " + toBigDecimal(cpi.get("cpiGradePoints")));
            System.out.println("Final totalCpiRegisteredCredits CPI: " + toBigDecimal(cpi.get("cpiRegisteredCredits")));
            System.out.println("Final Cumulative CPI: " + cpi.get("CPI"));

            StudentSemesterResult ssr = new StudentSemesterResult();
            ssr.setSsrid(ssrid);
            ssr.setSsrsrgid(srg.getSrgid());
            ssr.setSsrcreditsregistered(toBigDecimal(spi.get("creditsRegistered")));
            ssr.setSsrcreditsearned(toBigDecimal(spi.get("creditsEarned")));
            ssr.setSsrgradepointsearned(toBigDecimal(spi.get("gradePointsEarned")));
            ssr.setSsrcumcreditsregistered(toBigDecimal(cpi.get("cpicreditsRegistered")));
            ssr.setSsrcumcreditsearned(toBigDecimal(cpi.get("cpicreditsEarned")));
            ssr.setSsrcumgradepointsearned(toBigDecimal(cpi.get("cpigradePointsEarned")));
            ssr.setSsrspigradepoint(toBigDecimal(spi.get("spiGradePoints")));
            ssr.setSsrregisteredcredits(toBigDecimal(spi.get("spiRegisteredCredits")));
            ssr.setSsrcpigradepoints(toBigDecimal(cpi.get("cpiGradePoints")));
            ssr.setSsrcpiregisteredcredits(toBigDecimal(cpi.get("cpiRegisteredCredits")));
            ssr.setSsrspi(String.valueOf(spi.get("semesterSPI")));
            ssr.setSsrcpi(String.valueOf(cpi.get("CPI")));
            ssr.setSsrfield1(null);
            ssr.setSsrfield2(null);
            ssr.setSsrfield3(null);
            ssr.setSsrcreatedat(LocalDateTime.now());
            ssr.setSsrcreatedby(7L);
            ssr.setSsrlastupdatedat(null);
            ssr.setSsrlastupdatedby(null);
            ssr.setSsrrowstate(1L);
            ssr.setSsrcpiNumeric(toBigDecimal(cpi.get("CPI")));
            ssr.setSsrspiNumeric(toBigDecimal(spi.get("semesterSPI")));
            ssr.setGrade(null);
            ssrRepo.save(ssr);

            ssrid+=1;
        }
    }

    private Map<String, Object> computeSPI(Long strid, Long stdid, Long srgid){

        // Fetch the sums for the current semester
        List<Object[]> totals = stdRegCrsRepo.getSpiTotals(srgid, stdid);
        List<Object[]> totalCreditsRegistered = stdRegCrsRepo.getSpiCreditsRegistered(srgid, stdid);
        List<Object[]> totalCreditsEarned = stdRegCrsRepo.getSpiCreditsEarned(srgid, stdid);

        String semesterSPI = "0.00"; // Default value
        float creditsRegistered=0, creditsEarned=0, gradePointsEarned=0, spiGradePoints=0, spiRegisteredCredits=0;

        if (totalCreditsRegistered != null && !totalCreditsRegistered.isEmpty()) {
            Object[] row = totalCreditsRegistered.get(0);
            if(row != null && row[0] != null) {
                creditsRegistered = ((Number) row[0]).floatValue();
            }
        }
        if (totalCreditsEarned != null && !totalCreditsEarned.isEmpty()) {
            Object[] row = totalCreditsEarned.get(0);
            if(row != null && row[0] != null) {
                creditsEarned = ((Number) row[0]).floatValue();
            }
        }

        if (totals != null && !totals.isEmpty()) {
            Object[] row = totals.get(0);
            if(row != null && row[0] != null && row[1] != null) {

                spiGradePoints = ((Number) row[0]).floatValue(); // Index 0: TOTOBTCREDITS (spiGradePoints)
                spiRegisteredCredits = ((Number) row[1]).floatValue(); // Index 1: TOTCRSCREDITS (spiRegisteredCredits)
                gradePointsEarned = spiGradePoints;

                if (spiRegisteredCredits > 0) {
                    float semesterStudentSPI = spiGradePoints / spiRegisteredCredits;
                    
                    // Formatting to 2 decimal places
                    semesterSPI = String.format("%.2f", semesterStudentSPI);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("creditsRegistered", creditsRegistered);
        result.put("creditsEarned", creditsEarned);
        result.put("gradePointsEarned", gradePointsEarned);
        result.put("spiGradePoints", spiGradePoints);
        result.put("spiRegisteredCredits", spiRegisteredCredits);
        result.put("semesterSPI", semesterSPI);

        return result;

    }

    private List<Long> computeCourses(Long strid, Long stdid, Long srgid){

        // All Courses For a Student in a Particular Sem
        List<Object[]> crsInASem = semRepo.findCrsForStdAndSem(stdid,strid);

        Long lastProcessedCrsId = 0L;
        List<Long> eligibleTermCoursesList = new ArrayList<>();

        for (Object[] row : crsInASem) {
            Long currentCrsId = ((Number) row[7]).longValue(); // TCRCRSID
            Long termCourseId = ((Number) row[5]).longValue(); // TCRID
            String registrationType = row[4] != null ? row[4].toString().trim() : ""; // SRCTYPE

            // Skip if we've already processed this course (takes the latest because of SQL ORDER BY)
            if (currentCrsId.equals(lastProcessedCrsId)) {
                continue; 
            } else {
                lastProcessedCrsId = currentCrsId;
            }

            // Handle AUDIT courses
            if (registrationType.equalsIgnoreCase("AUDIT")) {
                // Check if student passed ('P' grade) using your specific table/logic
                Long passCount = egcrstt1Repo.countPassGradesForAudit(stdid, termCourseId);
                if (passCount == 0) {
                    continue;
                }
            }

            // Add to the List
            eligibleTermCoursesList.add(termCourseId);
        }

        if(!eligibleTermCoursesList.isEmpty()){

            // Fetch the courses to be eliminated (Lieu Courses)
            List<Long> lieuCrs = lieuCrsRepo.findForStd(stdid,eligibleTermCoursesList);

            // Remove all eliminated IDs from our main list
            if (lieuCrs != null && !lieuCrs.isEmpty()) {
                eligibleTermCoursesList.removeAll(lieuCrs);
            }
        }
        System.out.println("Final Eligible List for Calculation: " + eligibleTermCoursesList);
        return eligibleTermCoursesList;

    }

    private Map<String, Object> computeCPI(Long stdid, List<Long> eligibleCourseIds){

        float cumCreditsRegistered = 0;
        float cumCreditsEarned = 0;
        float cumGradePointsEarned = 0;
        float totalCpiGradePoints = 0;
        float totalCpiRegisteredCredits = 0;
        String semesterCPI = "0.00";

        if (!eligibleCourseIds.isEmpty()) {

            // Fetch totals for all eligible courses in one single database hit
            List<Object[]> cpiTotals = stdRegCrsRepo.getCpiTotals(stdid, eligibleCourseIds);
            List<Object[]> cpiCreditsRegistered = stdRegCrsRepo.getCpiCreditsRegistered(stdid, eligibleCourseIds);
            List<Object[]> cpiCreditsEarned = stdRegCrsRepo.getCpiCreditsEarned(stdid, eligibleCourseIds);

            if (cpiCreditsRegistered != null && !cpiCreditsRegistered.isEmpty()) {
                Object[] row = cpiCreditsRegistered.get(0);
                if(row != null && row[0] != null) {
                    cumCreditsRegistered = ((Number) row[0]).floatValue();
                }
            }

            if (cpiCreditsEarned != null && !cpiCreditsEarned.isEmpty()) {
                Object[] row = cpiCreditsEarned.get(0);
                if(row != null && row[0] != null) {
                    cumCreditsEarned = ((Number) row[0]).floatValue();
                }
            }

            if (cpiTotals != null && !cpiTotals.isEmpty()) {

                Object[] row = cpiTotals.get(0);

                if(row != null && row[0] != null && row[1] != null) {

                    totalCpiGradePoints = ((Number) row[0]).floatValue(); // CPIGRADEPOINTS
                    totalCpiRegisteredCredits = ((Number) row[1]).floatValue(); // CPIREGISTEREDCREDITS
                    cumGradePointsEarned = totalCpiGradePoints;

                    if (totalCpiRegisteredCredits > 0) {
                        float cumulativeCPI = totalCpiGradePoints / totalCpiRegisteredCredits;
                        semesterCPI = String.format("%.2f", cumulativeCPI);
                    }
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cpicreditsRegistered", cumCreditsRegistered);
        result.put("cpicreditsEarned", cumCreditsEarned);
        result.put("cpigradePointsEarned", cumGradePointsEarned);
        result.put("cpiGradePoints", totalCpiGradePoints);
        result.put("cpiRegisteredCredits", totalCpiRegisteredCredits);
        result.put("CPI", semesterCPI);

        return result;

    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof String) return new BigDecimal((String) value);
        if (value instanceof Number) {
            // Convert to double first to use the valueOf factory (prevents precision noise)
            return BigDecimal.valueOf(((Number) value).doubleValue())
                            .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

}

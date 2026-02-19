package com.ecampus.repository;

import com.ecampus.model.DropdownItem;
import com.ecampus.model.TermCourses;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface TermCoursesRepository extends JpaRepository<TermCourses, Long> {

//    @Query(value = "SELECT * FROM ec2.TERMCOURSES trmsrc WHERE trmsrc.TCRID = :trmcrsid AND trmsrc.TCRROWSTATE > 0", nativeQuery = true)
//    TermCourses getbytrmcrsid(@Param("trmcrsid") Long termcourseId);

//    @Query(value = "SELECT * FROM ec2.TERMCOURSEAVAILABLEFOR tca, ec2.TERMCOURSES tc, ec2.COURSES c WHERE tca.TCAPRGID = :prgId AND tca.TCATCRID = tc.TCRID AND tc.TCRCRSID = c.CRSID AND tca.TCASTATUS = 'T' AND tca.TCAROWSTATE > 0 AND tc.TCRROWSTATE > 0 AND c.CRSROWSTATE > 0 AND tc.TCRTRMID = :trmId ORDER BY c.CRSNAME", nativeQuery = true)
//    List<TermCourses> getTCourses(@Param("prgId") Long prgId, @Param("trmId") Long trmId);

//    @Query(value = "SELECT * FROM ec2.TERMCOURSEAVAILABLEFOR tca, ec2.TERMCOURSES tc, ec2.COURSES c WHERE tca.TCAPRGID = :prgId AND tca.TCATCRID = tc.TCRID AND tc.TCRCRSID = c.CRSID AND tca.TCASTATUS = 'T' AND tca.TCAROWSTATE > 0 AND tc.TCRROWSTATE > 0 AND c.CRSROWSTATE > 0 AND tc.TCRTRMID = :trmId AND tc.TCRID NOT IN (SELECT DISTINCT scr.SCRTCRID FROM ec2.SEMESTERCOURSES scr WHERE scr.SCRSTRID = :semesterId AND scr.SCRROWSTATE > 0 AND scr.SCRTCRID IS NOT NULL) ORDER BY c.CRSNAME", nativeQuery = true)
//    List<TermCourses> getOCourses(@Param("semesterId") Long semesterId, @Param("prgId") Long prgId, @Param("trmId") Long trmId);

//    @Query(value = "SELECT * FROM ec2.TERMCOURSES tc, ec2.COURSES c, ec2.TERMCOURSEAVAILABLEFOR tca WHERE tc.TCRID = tca.TCATCRID AND c.CRSID = tc.TCRCRSID AND c.CRSROWSTATE > 0 AND tca.TCAROWSTATE > 0 AND tc.TCRROWSTATE > 0 AND tc.TCRTRMID = :trmId AND tca.TCAPRGID = :prgId AND tca.TCASTATUS = 'T' AND tc.TCRSTATUS = 'AVAILABLE' AND c.CRSID IN (SELECT DISTINCT scr.SCRCRSID FROM ec2.SEMESTERCOURSES scr WHERE scr.SCRSTRID <> :semesterId AND scr.SCRCRSID IS NOT NULL AND scr.SCRROWSTATE > 0 AND scr.SCRSTRID IN (SELECT str.STRID FROM ec2.SEMESTERS str WHERE str.STRROWSTATE > 0 AND str.STRFIELD3 = 'T' AND str.STRBCHID = :batchId) AND scr.SCRCRSID NOT IN (SELECT DISTINCT tcr.TCRCRSID FROM ec2.TERMCOURSES tcr WHERE tcr.TCRID IN (SELECT egc1.TCRID FROM ec2.EGCRSTT1 egc1 WHERE egc1.STUD_ID = :studentId AND egc1.ROW_ST > '0' AND egc1.OBTGR_ID IN (1,2,3,4,10,11,15,16,17,18,19,20,21,22) AND tcr.TCRROWSTATE > 0)) UNION SELECT DISTINCT tcr.TCRCRSID AS CRSID FROM ec2.TERMCOURSES tcr WHERE tcr.TCRID IN (SELECT src.SRCTCRID FROM ec2.STUDENTREGISTRATIONS srg, ec2.STUDENTREGISTRATIONCOURSES src, ec2.EGCRSTT1 egc1 WHERE srg.SRGSTDID = :studentId AND src.SRCTCRID = egc1.TCRID AND srg.SRGSTDID = egc1.STUD_ID AND srg.SRGID = src.SRCSRGID AND srg.SRGSTRID <> :semesterId AND src.SRCSTATUS = 'ACTIVE' AND src.SRCTYPE <> 'AUDIT' AND srg.SRGROWSTATE > 0 AND src.SRCROWSTATE > 0 AND egc1.ROW_ST > '0' AND egc1.OBTGR_ID IN (5,7,14)) AND tcr.TCRROWSTATE > 0)", nativeQuery = true)
//    List<TermCourses> getOBCourses(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId, @Param("prgId") Long prgId, @Param("trmId") Long trmId, @Param("batchId") Long batchId);

//    @Query(value = "SELECT * FROM ec2.TERMCOURSES tc,ec2.TERMCOURSEAVAILABLEFOR tca, ec2.COURSES c WHERE tca.TCAPRGID = :prgId AND tca.TCATCRID = tc.TCRID AND tc.TCRCRSID = c.CRSID AND tca.TCASTATUS = 'T' AND tca.TCAROWSTATE > 0 AND tc.TCRROWSTATE > 0 AND c.CRSROWSTATE > 0 AND tc.TCRTRMID = :trmId AND tc.TCRCRSID NOT IN (SELECT DISTINCT tc2.TCRCRSID FROM ec2.STUDENTREGISTRATIONS sr, ec2.STUDENTREGISTRATIONCOURSES src, ec2.TERMCOURSES tc2 WHERE sr.SRGID = src.SRCSRGID AND src.SRCTCRID = tc2.TCRID AND src.SRCSTATUS = 'ACTIVE' AND src.SRCROWSTATE > 0 AND sr.SRGROWSTATE > 0 AND tc2.TCRROWSTATE > 0 AND sr.SRGSTDID = :studentId AND sr.SRGSTRID <> :semesterId) AND tc.TCRCRSID NOT IN (SELECT cgc.CGCCRSID FROM ec2.SEMESTERCOURSES sc, ec2.COURSEGROUPCOURSES cgc WHERE sc.SCRSTRID = :semesterId AND sc.SCRCGPID = cgc.CGCCGPID AND sc.SCRROWSTATE > 0 AND cgc.CGCROWSTATE > 0) AND tc.TCRCRSID NOT IN (SELECT sc2.SCRCRSID FROM ec2.SEMESTERCOURSES sc2 WHERE sc2.SCRSTRID = :semesterId AND sc2.SCRROWSTATE > 0 AND sc2.SCRELECTIVE = 'N' AND sc2.SCRCRSID IS NOT NULL) ORDER BY c.CRSNAME", nativeQuery = true)
//    List<TermCourses> getOTermCourses(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId, @Param("prgId") Long prgId, @Param("trmId") Long trmId);

//    @Query(value = "SELECT * FROM ec2.TERMCOURSEAVAILABLEFOR tca, ec2.TERMCOURSES tc, ec2.COURSES c WHERE tca.TCAPRGID = :prgId AND tc.TCRTRMID = :trmId AND tca.TCATCRID = tc.TCRID AND tc.TCRCRSID = c.CRSID AND tc.TCRCRSID IN (SELECT tc2.TCRCRSID FROM ec2.EGCRSTT1 e, ec2.TERMCOURSES tc2 WHERE tc2.TCRID = e.TCRID AND (e.OBTGR_ID = 4 OR e.OBTGR_ID = 21 OR e.OBTGR_ID = 22) AND e.ROW_ST > '0' AND e.STUD_ID = :studentId AND tc2.TCRROWSTATE > 0 AND tc2.TCRCRSID = c.CRSID AND c.CRSROWSTATE > 0) AND tc.TCRROWSTATE > 0 AND c.CRSROWSTATE > 0 ORDER BY c.CRSNAME", nativeQuery = true)
//    List<TermCourses> getGICourses(@Param("studentId") Long studentId, @Param("prgId") Long prgId, @Param("trmId") Long trmId);

//    @Query(value = "SELECT tc.TCRID FROM ec2.TERMCOURSES tc WHERE tc.TCRCRSID = :crsId AND tc.TCRTRMID = :trmId AND tc.TCRROWSTATE > 0", nativeQuery = true)
//    Long findTcrid(@Param("crsId") Long crsId, @Param("trmId") Long trmId);

//    @Query(value = "SELECT MAX(tcr.TCRID) FROM ec2.TERMCOURSES tcr", nativeQuery = true)
//    Long findMaxTermCourseid();

    @Query(value = """
            SELECT a.ayrname as ayrname, t.trmname AS term, c.crscode AS crscode, c.crsname AS crsname, CONCAT(c.crscreditpoints, ' (', c.crslectures, ' + ', c.crstutorials, ' + ', c.crspracticals, ')') AS credithours FROM ec2.termcourses AS tc
            JOIN ec2.terms AS t
            ON tc.tcrtrmid=t.trmid
            JOIN ec2.academicyears AS a
            ON t.trmayrid=a.ayrid
            JOIN ec2.courses AS c
            ON tc.tcrcrsid=c.crsid
            ORDER BY a.ayrid DESC, t.trmid DESC, tc.tcrid DESC
            """, nativeQuery = true)
    List<Object[]> getAllTermCoursesDetailsRaw();

    List<TermCourses> findByTerm_Trmid(Long termId);

    @Query("SELECT tc.tcrid FROM TermCourses tc " +
            "WHERE tc.course.crsid = :crsid AND tc.term.trmid = :trmid")
    Long findTcridByCrsidAndTrmid(@Param("crsid") Long crsid, @Param("trmid") Long trmid);

    List<TermCourses> findByTerm_TrmidAndUser_Uid(Long trmId, Long userId);

    List<TermCourses> findByTerm_AcademicYear_AyridAndTerm_Trmid(Long academicYearId, Long termId);

//    @Query("SELECT NEW com.ecampus.model.DropdownItem(CAST(tc.tcrid AS string), " +
//            "CONCAT(c.crsname, '-(', c.crscode, ')')) " +
//            "FROM TermCourses tc JOIN tc.course c " +
//            "WHERE (tc.tcrrowstate > 0 OR tc.tcrrowstate IS NULL) " +
//            "AND (c.crsrowstate > 0 OR c.crsrowstate IS NULL) " +
//            "AND tc.term.trmid = :termId " +
//            "AND EXISTS (SELECT 1 FROM Egcrstt1 e WHERE e.id.tcrid = tc.tcrid AND e.rowStatus = '1') " +
//            "ORDER BY c.crsname")
//    List<DropdownItem> findSubmittedTermCoursesByTermId(@Param("termId") Long termId);

//    @Query("SELECT NEW com.ecampus.model.DropdownItem(CAST(tc.tcrid AS string), " +
//            "CONCAT(c.crsname, '-(', c.crscode, ')')) " +
//            "FROM TermCourses tc JOIN tc.course c " +
//            "WHERE (tc.tcrrowstate > 0 OR tc.tcrrowstate IS NULL) " +
//            "AND (c.crsrowstate > 0 OR c.crsrowstate IS NULL) " +
//            "AND tc.term.trmid = :termId " +
//            "AND EXISTS (SELECT 1 FROM Egcrstt1 e WHERE e.id.tcrid = tc.tcrid " +
//            "AND e.updatedBy IS NOT NULL AND e.updatedDate IS NOT NULL) " +
//            "ORDER BY c.crsname")
//    List<DropdownItem> findUpdatedTermCoursesByTermId(@Param("termId") Long termId);

    // This method is for showUpdatedGradeListReport to get TermCourse details
    List<TermCourses> findByTerm_TrmidAndTcrrowstateGreaterThan(Long termId, int rowstate);

//         @Query(value = """
//         SELECT
//             t.*,
//             c.crscode,
//             c.crstitle,
//             c.crscreditpoints
//         FROM ec2.termcourses t
//         JOIN ec2.courses c
//             ON t.tcrcrsid = c.crsid
//         WHERE t.tcrtrmid = :termId
//     """, nativeQuery = true)
//     List<Map<String, Object>> findByTermWithCourseDetails(@Param("termId") Long termId);

    @Query("SELECT t FROM TermCourses t " +
            "WHERE t.term.trmid = :trmid " +
            "AND t.tcrfacultyid = :facultyId " +
            "AND t.tcrrowstate > 0 " +
            "ORDER BY t.course.crsid")
    List<TermCourses> findFacultyCoursesInTerm(
            @Param("trmid") Long trmid,
            @Param("facultyId") Long facultyId
    );

    @Query(value = """
        SELECT
            t.*,
            c.crscode,
            c.crstitle,
            c.crscreditpoints
        FROM ec2.termcourses t
        JOIN ec2.courses c ON t.tcrcrsid = c.crsid
        WHERE t.tcrtrmid = :termId
    """, nativeQuery = true)
    List<TermCourses> findByTermWithCourseDetails(@Param("termId") Long termId);

//     boolean existsByTcrtrmidAndTcrcrsid(Long tcrtrmid, Long tcrcrsid);

//     @Query("SELECT COALESCE(MAX(t.tcrid), 0) FROM TermCourses t")
//     Long findMaxTcrid();

    List<TermCourses> findByTcrtrmidOrderByTcrid(Long trmid);

    boolean existsByTcrtrmid(Long trmid);

    @Query("SELECT COALESCE(MAX(t.tcrid), 0) FROM TermCourses t")
    Long findMaxTcrid();

    @Modifying
    @Transactional
    void deleteByTcrid(Long tcrid);

    boolean existsByTcrtrmidAndTcrcrsid(Long trmid, Long crsid);

    @Query("SELECT t FROM TermCourses t WHERE t.tcrid = :tcrid")
    TermCourses findByTcrid(@Param("tcrid") Long tcrid);

    @Query(value = """
    SELECT
        t.tcrfacultyid AS facultyId,
        u.uemail AS facultyEmail,
        COUNT(t.tcrid) AS assignedCount
    FROM ec2.termcourses t
    JOIN ec2.users u ON u.uid = t.tcrfacultyid
    WHERE t.tcrfacultyid IS NOT NULL
      AND t.tcrtrmid = :termId
    GROUP BY t.tcrfacultyid, u.uemail
    ORDER BY assignedCount DESC
    """, nativeQuery = true)
    List<Object[]> getFacultyAssignmentSummaryForTerm(@Param("termId") Long termId);

    @Query(value = """
    SELECT c.crsid, c.crsname, c.crstitle, t.tcrslot
    FROM ec2.termcourses t
    JOIN ec2.courses c ON c.crsid = t.tcrcrsid
    WHERE t.tcrfacultyid = :facultyId
      AND t.tcrtrmid = :termId
    ORDER BY c.crscode
    """, nativeQuery = true)
    List<Object[]> getCoursesForFacultyInTerm(@Param("facultyId") Long facultyId,
                                              @Param("termId") Long termId);

    @Query(value = """
        SELECT
            src.SRCTCRID AS tcrid,
            COUNT(*)     AS studentCount
        FROM ec2.STUDENTREGISTRATIONCOURSES src
        JOIN ec2.STUDENTREGISTRATIONS srg
          ON src.SRCSRGID = srg.SRGID
        WHERE src.SRCTCRID IN (:tcrids)
          AND src.SRCROWSTATE > 0          -- active course registrations
          AND src.SRCSTATUS = 'ACTIVE'     -- active status
          AND (src.SRCTYPE IS NULL OR src.SRCTYPE <> 'AUDIT') -- no audit if you want
          AND srg.SRGROWSTATE > 0          -- active student registrations
        GROUP BY src.SRCTCRID
        """,
            nativeQuery = true)
    List<Object[]> countStudentsForTermCourses(@Param("tcrids") List<Long> tcrids);

    List<TermCourses> findByTcrtrmidAndCrstype(Long tcrtrmid, String crstype);

}

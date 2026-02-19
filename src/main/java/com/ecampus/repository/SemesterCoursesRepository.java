package com.ecampus.repository;

import com.ecampus.model.SemesterCourses;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;
import org.springframework.data.repository.query.Param;

@Repository
public interface SemesterCoursesRepository extends JpaRepository<SemesterCourses, Long> {
    // Custom queries can be added here
    @Query(value = "SELECT * FROM ec2.SEMESTERCOURSES sc, ec2.COURSES c, ec2.SEMESTERS s WHERE sc.SCRROWSTATE > 0 AND s.STRROWSTATE > 0 AND c.CRSROWSTATE > 0 AND c.CRSID = sc.SCRCRSID AND s.STRID = sc.SCRSTRID AND sc.SCRELECTIVE = 'N' AND sc.SCRSTRID = :semesterId", nativeQuery = true)
    List<SemesterCourses> getccbysemid(@Param("semesterId") Long semesterId);

    @Query(value = "SELECT * FROM ec2.SEMESTERCOURSES sc, ec2.COURSES c, ec2.SEMESTERS s WHERE sc.SCRROWSTATE > 0 AND s.STRROWSTATE > 0 AND c.CRSROWSTATE > 0 AND c.CRSID = sc.SCRCRSID AND s.STRID = sc.SCRSTRID AND sc.SCRELECTIVE = 'Y' AND s.STRBCHID = :batchId AND s.STRID < :semesterId AND sc.SCRID NOT IN (SELECT SRC.SRCSCRID FROM ec2.STUDENTREGISTRATIONS SRG, ec2.STUDENTREGISTRATIONCOURSES SRC, ec2.STUDENTSCORES SSC WHERE SRC.SRCROWSTATE > 0 AND SRG.SRGROWSTATE > 0 AND SSC.SSCROWSTATE > 0 AND SRC.SRCSRGID = SRG.SRGID AND SRC.SRCTCRID = SSC.SSCTCRID AND SSC.SSCSTDID = SRG.SRGSTDID AND SRG.SRGSTDID = :studentId)", nativeQuery = true)
    List<SemesterCourses> getBCCourses(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId, @Param("batchId") Long batchId);

    @Query(value = "SELECT * FROM ec2.SEMESTERCOURSES sc, ec2.COURSEGROUPS cg, ec2.SEMESTERS s WHERE sc.SCRROWSTATE > 0 AND s.STRROWSTATE > 0 AND cg.CGPROWSTATE > 0 AND cg.CGPID = sc.SCRCGPID AND s.STRID = sc.SCRSTRID AND sc.SCRELECTIVE = 'Y' AND s.STRBCHID = :batchId AND s.STRID < :semesterId AND sc.SCRID NOT IN (SELECT SRC.SRCSCRID FROM ec2.STUDENTREGISTRATIONS SRG, ec2.STUDENTREGISTRATIONCOURSES SRC, ec2.STUDENTSCORES SSC WHERE SRC.SRCROWSTATE > 0 AND SRG.SRGROWSTATE > 0 AND SSC.SSCROWSTATE > 0 AND SRC.SRCSRGID = SRG.SRGID AND SRC.SRCTCRID = SSC.SSCTCRID AND SSC.SSCSTDID = SRG.SRGSTDID AND SRG.SRGSTDID = :studentId)", nativeQuery = true)
    List<SemesterCourses> getBECourses(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId, @Param("batchId") Long batchId);

//    @Query(value = "SELECT sc.SCRID FROM ec2.SEMESTERCOURSES sc WHERE sc.SCRSTRID = :semesterId AND sc.SCRCRSID = :courseId", nativeQuery = true)
//    Long findScrid(@Param("semesterId") Long semesterId, @Param("courseId") Long courseId);

    // Fetch one semester course entry by SCRID
//    @Query(value = "SELECT * FROM ec2.SEMESTERCOURSES scr " +
//            "WHERE scr.SCRID = :scrid AND scr.SCRROWSTATE > 0",
//            nativeQuery = true)
//    SemesterCourses getByScrid(@Param("scrid") Long scrid);

    // Get all semester courses for a given semester (STRID)
//    @Query(value = "SELECT * FROM ec2.SEMESTERCOURSES scr " +
//            "WHERE scr.SCRSTRID = :semesterId AND scr.SCRROWSTATE > 0 " +
//            "ORDER BY scr.SCRID",
//            nativeQuery = true)
//    List<SemesterCourses> getSemesterCourses(@Param("semesterId") Long semesterId);

    // Get max SCRID for manual ID generation
//    @Query(value = "SELECT MAX(scr.SCRID) FROM ec2.SEMESTERCOURSES scr",
//            nativeQuery = true)
//    Long findMaxSemesterCourseid();

    @Query(value = """
            SELECT CONCAT(t.trmname, ' (', a.ayrname, ')') as term, CONCAT(COALESCE(sd.spldesc, p.prgname), ' ', b.bchname, ' - ', s.strname) AS batchsem, c.crscode AS crscode, c.crsname AS crsname, CONCAT(c.crscreditpoints, ' (', c.crslectures, ' + ', c.crstutorials, ' + ', c.crspracticals, ')') AS credithours FROM ec2.semestercourses AS sc
            JOIN ec2.semesters AS s
            ON sc.scrstrid=s.strid
            JOIN ec2.batches AS b
            ON s.strbchid=b.bchid
            JOIN ec2.programs AS p
            ON b.bchprgid=p.prgid
            LEFT JOIN ec2.schemedetails as sd
            ON b.scheme_id=sd.scheme_id AND b.splid=sd.splid
            JOIN ec2.terms AS t
            ON s.strtrmid=t.trmid
            JOIN ec2.academicyears AS a
            ON t.trmayrid=a.ayrid
            JOIN ec2.courses AS c
            ON sc.scrcrsid=c.crsid
            ORDER BY t.trmid DESC, b.bchid DESC, sc.scrseqno ASC
            """, nativeQuery = true)
    List<Object[]> getAllSemesterCoursesDetailsRaw();
    
    @Query(value = "SELECT COALESCE(MAX(sc.SCRID), 0) FROM ec2.SEMESTERCOURSES sc", nativeQuery = true)
    Long findMaxSemesterCourseid();
}

package com.ecampus.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.ecampus.model.SchemeCourses;
import com.ecampus.model.SchemeCoursesId;

import java.util.List;

public interface SchemeCoursesRepository extends JpaRepository<SchemeCourses, SchemeCoursesId> {
    // Find all scheme courses for a scheme (all specializations)
    List<SchemeCourses> findBySchemeId(Long schemeId);

    // Find all scheme courses for a scheme limited to a set of splids, ordered by programYear and termSeqNo
    List<SchemeCourses> findBySchemeIdAndSplidInOrderByProgramYearAscTermSeqNoAsc(Long schemeId, List<Long> splids);

    // Find courses for specific splid, termName and programYear, ordered by courseSrNo
    List<SchemeCourses> findBySchemeIdAndSplidAndTermNameAndProgramYearOrderByCourseSrNo(
            Long schemeId, Long splid, String termName, Long programYear);
    
    // Find courses for multiple splids, termName and programYear, ordered by splid then courseSrNo
    List<SchemeCourses> findBySchemeIdAndSplidInAndTermNameAndProgramYearOrderBySplidAscCourseSrNoAsc(
            Long schemeId, List<Long> splids, String termName, Long programYear);

    // Find all scheme courses for a scheme, splid, and matching semester names
    List<SchemeCourses> findBySchemeIdAndSplidAndSemesterNameIn(Long schemeId, Long splid, List<String> semesterNames);

    // Find scheme courses for multiple splids and matching semester names
    @Query("SELECT sc FROM SchemeCourses sc WHERE sc.schemeId = :schemeId AND sc.splid IN :splids AND sc.semesterName IN :semesterNames ORDER BY sc.semesterName ASC, sc.courseSrNo ASC")
    List<SchemeCourses> findBySchemeIdAndSplidInAndSemesterNameIn(@Param("schemeId") Long schemeId, @Param("splids") List<Long> splids, @Param("semesterNames") List<String> semesterNames);

    @Query(value="SELECT tp.splname, sc.semester_name, ct.ctpcode, COUNT(*) AS total_count " +
                        "FROM ec2.schemecourses sc JOIN ec2.coursetypes ct ON sc.ctpid = ct.ctpid " +
                        "JOIN (SELECT sd.scheme_id, sd.splid, sd.splname " +
                        "FROM ec2.schemedetails sd " +
                        "WHERE sd.scheme_id = (SELECT scheme_id FROM ec2.schemedetails WHERE splname = :splname LIMIT 1)) " +
                        "tp ON sc.scheme_id = tp.scheme_id " +
                        "WHERE sc.crsid IS NULL " +
                        "AND (sc.splid = tp.splid OR sc.splid = 0) " +
                        "AND sc.semester_name = ( " +
                        "SELECT strname " +
                        "FROM ec2.semesters " +
                        "WHERE strtrmid = :trmid " +
                        "AND strbchid IN (SELECT bchid FROM ec2.batches WHERE bchname = :bchname) LIMIT 1) " +
                        "GROUP BY tp.splname, sc.semester_name, ct.ctpcode", nativeQuery = true)
    List<Object[]> findRequirementsForPrgInSem(@Param("bchname") String bchname, @Param("splname") String splname, @Param("trmid") Long trmid);
}

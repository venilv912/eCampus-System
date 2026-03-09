package com.ecampus.repository;

import com.ecampus.model.CourseTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseTypesRepository extends JpaRepository<CourseTypes, Long> {
    // Find all course types for a scheme (all specializations)
    List<CourseTypes> findBySchemeId(Long schemeId);

    // Find course types for a specific scheme and splid
    List<CourseTypes> findBySchemeIdAndSplid(Long schemeId, Long splid);

    // Find course types for a scheme restricted to specific splid values
    List<CourseTypes> findBySchemeIdAndSplidIn(Long schemeId, java.util.List<Long> splids);

//    // Legacy notes / examples
//    // List<CourseRequirement> findBySchemeId(Long schemeId);
//    // CourseRequirement findBySchemeIdAndCourseTypeCode(Long schemeId, String courseTypeCode);
//    // List<CourseRequirement> findByProgramIdAndSchemeId(Long programId, Long schemeId);

    @Query(value = "SELECT DISTINCT ct.ctpcode AS code, ct.ctpname AS name FROM ec2.coursetypes ct WHERE ct.crscat='ELECTIVE' AND ct.ctpid IN (SELECT DISTINCT sc.ctpid FROM ec2.schemecourses sc WHERE sc.scheme_id = :scheme_id AND sc.splid = :splid AND sc.sem_no <= :sem_no AND sc.crsid IS NULL)", nativeQuery = true)
    List<Object[]> getElectiveTypeBySchSplSem(@Param("scheme_id") Long scheme_id, @Param("splid") Long splid, @Param("sem_no") Long sem_no);

    @Query("SELECT COALESCE(MAX(c.ctpid), 0) FROM CourseTypes c")
    Long findMaxCtpid();
}

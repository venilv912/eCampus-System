package com.ecampus.repository;

import com.ecampus.model.Courses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, Long> {

//    @Query(value = "SELECT * FROM ec2.COURSES crs WHERE crs.CRSID = :crsid AND crs.CRSROWSTATE > 0", nativeQuery = true)
//    Courses getbycrsid(@Param("crsid") Long crsid);

//    @Query(value = "SELECT * FROM ec2.COURSEGROUPCOURSES cgc, ec2.COURSES crs WHERE cgc.CGCROWSTATE > 0 AND crs.CRSROWSTATE > 0 AND crs.CRSID = cgc.CGCCRSID AND cgc.CGCCGPID = :cgpId AND crs.CRSID IN (SELECT tc.tcrcrsid FROM ec2.TERMCOURSEAVAILABLEFOR tca, ec2.TERMCOURSES tc WHERE tca.tcaprgid = :prgId AND tca.tcatcrid = tc.tcrid AND tcastatus = 'T' AND tca.tcarowstate > 0 AND tc.tcrrowstate > 0 AND tc.tcrtrmid = :trmId) ORDER BY crs.crsname", nativeQuery = true)
//    List<Courses> getbycgpId(@Param("cgpId") Long cgpId, @Param("prgId") Long prgId, @Param("trmId") Long trmId);

    @Query("SELECT COALESCE(MAX(c.crsid), 0) FROM Courses c")
    Long findMaxCrsid();

//    @Query(value = "SELECT crs.CRSID FROM ec2.COURSES crs WHERE crs.CRSROWSTATE > 0 AND crs.CRSCODE = :courseCode", nativeQuery = true)
//    Long findCourseIdByName(@Param("courseCode") String courseCode);

    // Fetch archived courses where rowstate >= 900
    @Query("SELECT c FROM Courses c WHERE c.crsrowstate >= 900")
    List<Courses> findArchivedCourses();

    List<Courses> findByTerm_Trmid(Long trmId);
    List<Courses> findByCrsrowstateGreaterThan(Long rowState);
    Page<Courses> findByCrsrowstateGreaterThan(int rowState, Pageable pageable);

    @Query(
            value = """
        SELECT * FROM ec2.COURSES 
        WHERE CRSROWSTATE > 0 
          AND to_tsvector('english',
                coalesce(CRSNAME, '') || ' ' ||
                coalesce(CRSDISCIPLINE, '') || ' ' ||
                coalesce(CRSASSESSMENTTYPE, '') || ' ' ||
                coalesce(CRSCODE, '')
              ) @@ to_tsquery(
                    'english',
                    regexp_replace(:keyword, '\\s+', ' & ', 'g') || ':*'
              )
        ORDER BY CRSNAME
        """,
            countQuery = """
        SELECT count(*) FROM ec2.COURSES 
        WHERE CRSROWSTATE > 0 
          AND to_tsvector('english',
                coalesce(CRSNAME, '') || ' ' ||
                coalesce(CRSDISCIPLINE, '') || ' ' ||
                coalesce(CRSASSESSMENTTYPE, '') || ' ' ||
                coalesce(CRSCODE, '')
              ) @@ to_tsquery(
                    'english',
                    regexp_replace(:keyword, '\\s+', ' & ', 'g') || ':*'
              )
        """,
            nativeQuery = true
    )
    Page<Courses> searchCourses(@Param("keyword") String keyword, Pageable pageable);

    Page<Courses> findByCrsrowstateEquals(int rowState, Pageable pageable);

    @Query(value = """
SELECT crs.*
FROM ec2.COURSEGROUPCOURSES cgc
JOIN ec2.COURSES crs ON crs.CRSID = cgc.CGCCRSID
WHERE cgc.CGCROWSTATE > 0
  AND crs.CRSROWSTATE > 0
  AND cgc.CGCCGPID = :cgpid
  AND crs.CRSID IN (
      SELECT tc.tcrcrsid
      FROM ec2.TERMCOURSEAVAILABLEFOR tca
      JOIN ec2.TERMCOURSES tc ON tca.tcatcrid = tc.tcrid
      WHERE tca.tcaprgid = :prgid
        AND tcastatus = 'T'
        AND tca.tcarowstate > 0
        AND tc.tcrrowstate > 0
        AND tc.tcrtrmid = :trmid
  )
""", nativeQuery = true)
    List<Courses> findCoursesByGroupProgramAndTerm(@Param("cgpid") Long cgpid,@Param("prgid") Long prgid,@Param("trmid") Long trmid);

    List<Courses> findByCrsnameContainingIgnoreCaseOrCrscodeContainingIgnoreCase(String name, String code);

    @Query("SELECT c FROM Courses c WHERE LOWER(c.crsname) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.crscode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Courses> searchCourses(@Param("query") String query);

    @Query(value = "select crs.crscode,crs.crsname,CONCAT(crs.crslectures, '-', crs.crstutorials, '-', crs.crspracticals, '-', crs.crscreditpoints) as ltpc from ec2.courses crs where crs.crscode in (:ids)", nativeQuery = true)
    List<Object[]> findCrsByListTcrids(@Param("ids") List<String> ids);

}

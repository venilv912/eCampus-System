package com.ecampus.repository;

import com.ecampus.model.TermCourseAvailableFor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TermCourseAvailableForRepository extends JpaRepository<TermCourseAvailableFor, Long> {

    @Query(value = "SELECT * FROM ec2.termcourseavailablefor tca WHERE tca.tcabchid = :tcabchid AND tca.tcatcrid IN (SELECT tc.tcrid FROM ec2.termcourses tc WHERE tc.crstype = 'ELECTIVE' AND tc.tcrtrmid = :tcrtrmid)", nativeQuery = true)
    List<TermCourseAvailableFor> getCrsByBch(@Param("tcrtrmid") Long tcrtrmid, @Param("tcabchid") Long tcabchid);

    @Query(value = "SELECT MAX(tca.tcaid) FROM ec2.termcourseavailablefor tca", nativeQuery = true)
    Long findMaxTcaid();

    @Query(value = "SELECT c.crscode, sd.splname, sem.strname, tca.tcaelectivetype, tca.tca_seats " +
                "FROM ec2.termcourseavailablefor tca " +
                "JOIN ec2.termcourses tc ON tca.tcatcrid = tc.tcrid " +
                "JOIN ec2.courses c ON tc.tcrcrsid = c.crsid " +
                "JOIN ec2.batches b ON tca.tcabchid = b.bchid " +
                "JOIN ec2.schemedetails sd ON b.scheme_id = sd.scheme_id AND b.splid = sd.splid " +
                "JOIN ec2.semesters sem ON b.bchid = sem.strbchid " +
                "WHERE tc.tcrtrmid = :trmid " +
                "AND b.bchname = :bchname " +
                "AND sd.splname = :splname " +
                "AND sem.strtrmid = :trmid AND tca.tcaelectivetype IS NOT NULL", nativeQuery = true)
    List<Object[]> findAvailableElectivesDetails(@Param("trmid") Long trmid, 
                                                @Param("bchname") String bchname, 
                                                @Param("splname") String splname);
    
}
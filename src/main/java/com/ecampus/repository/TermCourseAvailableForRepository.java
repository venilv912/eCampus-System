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
    
}
package com.ecampus.repository;

import com.ecampus.model.TermCourseAvailableFor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
                "AND sem.strtrmid = :trmid AND tca.tcaelectivetype IS NOT NULL AND tc.tcrslot IS NOT NULL", nativeQuery = true)
    List<Object[]> findAvailableElectivesDetails(@Param("trmid") Long trmid, 
                                                @Param("bchname") String bchname, 
                                                @Param("splname") String splname);

    @Query(value = "SELECT * FROM ec2.termcourseavailablefor tca WHERE tca.tcatcrid = :tcrid AND tca.tcabchid = :bchid", nativeQuery = true)
    TermCourseAvailableFor findByTcridAndBchid(@Param("tcrid") Long tcrid, @Param("bchid") Long bchid);

    @Query(value = "select tca.tcatcrid from ec2.termcourseavailablefor tca join ec2.termcourses tc on tca.tcatcrid=tc.tcrid where tca.tcabchid=:bchid and tc.tcrtrmid=:trmid and tc.tcrslot is not null and tca.tcaelectivetype is not null", nativeQuery = true)
    List<Long> findByTrmAndBch(@Param("trmid") Long trmid, @Param("bchid") Long bchid);

    @Query(value = "select crs.crscode,sum(tca.tca_seats-tca.tca_booked) from ec2.termcourseavailablefor tca join ec2.termcourses tc on tca.tcatcrid=tc.tcrid join ec2.courses crs on tc.tcrcrsid=crs.crsid where tc.tcrtrmid=78 and tca.tcaelectivetype is not null and tc.tcrslot is not null group by tca.tcatcrid,crs.crscode", nativeQuery = true)
    List<Object[]> getForAddDrop();

    @Modifying
    @Transactional
    @Query("UPDATE TermCourseAvailableFor t SET t.tca_booked = t.tca_booked + 1 " + 
        "WHERE t.tcatcrid = :tcrid AND t.tcabchid = :bchid")
    void incrementBookedCount(@Param("tcrid") Long tcrid, @Param("bchid") Long bchid);

    @Modifying
    @Transactional
    @Query("UPDATE TermCourseAvailableFor t SET t.tca_booked = t.tca_booked - 1 " + 
        "WHERE t.tcatcrid = :tcrid AND t.tcabchid = :bchid")
    void decrementBookedCount(@Param("tcrid") Long tcrid, @Param("bchid") Long bchid);
    
}
package com.ecampus.repository;

import com.ecampus.model.SchemeDetails;
import com.ecampus.model.SchemeDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SchemeDetailsRepository extends JpaRepository<SchemeDetails, SchemeDetailsId> {
    List<SchemeDetails> findBySchemeId(Long schemeId);
    Optional<SchemeDetails> findBySchemeIdAndSplid(Long schemeId, Long splid);

    @Query(value = "SELECT DISTINCT scmdet.splname FROM ec2.schemedetails scmdet WHERE scmdet.scheme_id IN (SELECT DISTINCT bch.scheme_id FROM ec2.batches bch WHERE bch.bchname = :bchname)", nativeQuery = true)
    List<String> findProgramsByBatch(@Param("bchname") String bchname);

    @Query(value = "SELECT DISTINCT sd.splname FROM ec2.batches b JOIN ec2.schemedetails sd ON b.scheme_id = sd.scheme_id AND b.splid = sd.splid WHERE b.bchid IN (SELECT DISTINCT sm.strbchid FROM ec2.semesters sm WHERE sm.strtrmid = :trmid) ORDER BY sd.splname;", nativeQuery = true)
    List<String> findProgramsByTrm(@Param("trmid") Long trmid);

    @Query(value = "SELECT DISTINCT sd.splname FROM ec2.batches b JOIN ec2.schemedetails sd ON b.scheme_id = sd.scheme_id AND b.splid = sd.splid WHERE sd.splid = 0 AND b.bchid IN (SELECT DISTINCT sm.strbchid FROM ec2.semesters sm WHERE sm.strtrmid = :trmid) ORDER BY sd.splname;", nativeQuery = true)
    List<String> findMainProgramsByTrm(@Param("trmid") Long trmid);

    @Query(value = "SELECT scmdet.scheme_id FROM ec2.schemedetails scmdet WHERE scmdet.splname = :splname", nativeQuery = true)
    Long getSchIdFromSpl(@Param("splname") String splname);

    @Query(value = "SELECT scmdet.splid FROM ec2.schemedetails scmdet WHERE scmdet.splname = :splname", nativeQuery = true)
    Long getSplIdFromSpl(@Param("splname") String splname);

    @Query(value = "SELECT b.bchid, b.bchname, sd.scheme_id, sd.splname, ro.startdate, ro.enddate " +
               "FROM ec2.schemedetails sd " +
               "JOIN ec2.batches b ON b.scheme_id = sd.scheme_id AND b.splid = sd.splid " +
               "LEFT JOIN ec2.registrationopenfor ro ON b.bchid = ro.batchid AND ro.termid = :termId AND ro.registrationtype = :registrationtype " +
               "WHERE b.bchname IN :batches", nativeQuery = true)
    List<Object[]> getBchSpl(@Param("batches") List<String> batches, @Param("termId") Long termId, @Param("registrationtype") String registrationtype);

}

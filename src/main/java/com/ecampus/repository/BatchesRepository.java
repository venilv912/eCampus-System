package com.ecampus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecampus.model.Batches;
import com.ecampus.model.Programs;

@Repository
public interface BatchesRepository extends JpaRepository<Batches, Long> {

    @Query(value = "SELECT * FROM ec2.BATCHES btc WHERE btc.BCHROWSTATE > 0 AND btc.BCHID = :batchId", nativeQuery = true)
    Batches getbtchId(@Param("batchId") Long batchId);

    List<Batches> findByPrograms(Programs program);

    @Query("SELECT b FROM Batches b JOIN Programs p ON b.programs.prgid = p.prgid " +
            "WHERE b.bchrowstate > 0 AND p.prgrowstate > 0 ORDER BY p.prgfield1 ASC, b.bchfield1 ASC")
    List<Batches> findAllActiveBatchesOrderedByProgramAndBatchField();

    List<Batches> findByPrograms_PrgidAndBchrowstateGreaterThanOrderByBchfield1Asc(Long prgId, Long rowState);

    @Query("SELECT b FROM Batches b WHERE b.bchprgid = :prgid ORDER BY b.bchname")
    List<Batches> findByPrgId(@Param("prgid") Long prgid);

    @Query(value = "SELECT MAX(b.bchid) FROM Batches b", nativeQuery = true)
    Integer findMaxBatchId();

    List<Batches> findAllByOrderByBchidDesc();

    @Query(value = "SELECT b.bchid as bchid, a.ayrname as ayrname, p.prgname as prgname, b.bchname as bchname, b.bchcapacity as bchcapacity, s.effective_from_year as schemeyear FROM ec2.batches AS b JOIN ec2.academicyears AS a ON b.bchcalid=a.ayrid JOIN ec2.programs AS p ON b.bchprgid=p.prgid LEFT JOIN ec2.scheme AS s ON b.scheme_id=s.scheme_id ORDER BY b.bchid DESC", nativeQuery = true)
    List<Object[]> getAllBatchesDetailsRaw();
}

package com.ecampus.repository;

import com.ecampus.model.Terms;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {

//    @Query(value = "SELECT * FROM ec2.terms trm, ec2.academicyears acdy " +
//            "WHERE trm.trmrowstate > 0 AND acdy.ayrrowstate > 0 " +
//            "AND trm.trmayrid = acdy.ayrid " +
//            "AND trm.trmid = :termId", nativeQuery = true)
//    Terms gettrmId(@Param("termId") Long termId);

//    @Query(value = "SELECT trm.trmid FROM ec2.terms trm, ec2.academicyears acdy " +
//            "WHERE trm.trmrowstate > 0 AND acdy.ayrrowstate > 0 " +
//            "AND trm.trmayrid = acdy.ayrid " +
//            "AND trm.trmname = :name AND acdy.ayrid = :ayrid", nativeQuery = true)
//    Long findTermIdByName(@Param("name") String name, @Param("ayrid") Long ayrid);

    List<Terms> findByAcademicYear_Ayrid(Long academicYearId);

    @Query("SELECT DISTINCT t FROM Terms t WHERE t.trmrowstate > 0 ORDER BY t.trmname")
    List<Terms> findDistinctByTrmrowstateGreaterThan(int rowState);

    List<Terms> findByAcademicYear_AyridAndTrmrowstateGreaterThanOrderByTrmnameAsc(Long ayrId, int rowState);

    @Query(value = "SELECT DISTINCT trmname FROM ec2.terms ORDER BY trmname", nativeQuery = true)
    List<String> findDistinctTermNames();

    // @Query(value = "SELECT * FROM ec2.terms WHERE trmname = :name ORDER BY trmayrid DESC LIMIT 1", nativeQuery = true)
    // Terms findLatestTermByName(@Param("name") String trmname);

    // @Query("SELECT t FROM Terms t WHERE LOWER(t.trmname) = LOWER(:termName) ORDER BY t.trmayrid DESC LIMIT 1")
    // Terms findLatestTermByName(@Param("termName") String termName);

    @Query(value = "SELECT * FROM ec2.terms WHERE LOWER(trmname) = LOWER(:termName) ORDER BY trmayrid DESC LIMIT 1",
            nativeQuery = true)
    Terms findLatestTermByName(@Param("termName") String termName);

    @Query("""
    SELECT t
    FROM Terms t
    WHERE t.trmid = (
        SELECT MAX(t2.trmid)
        FROM Terms t2
        WHERE t2.trmrowstate > :rowState
    )
""")
    Terms findLatestMinusThree(@Param("rowState") int rowState);

//    @Query("SELECT MAX(t.trmid) FROM Terms t")
//    Long findMaxTrmid();

    @Query(value = "SELECT t.trmid AS trmid, t.trmname AS trmname, a.ayrname AS ayrname, t.trm_starts AS trmstarts, t.trm_ends AS trmends FROM ec2.terms AS t JOIN ec2.academicyears AS a ON t.trmayrid=a.ayrid ORDER BY t.trmid DESC", nativeQuery = true)
    List<Object[]> getAllTermsDetailsRaw();
    
    @Query("SELECT COALESCE(MAX(t.trmid), 0) FROM Terms t")
    Long findMaxTrmid();

    @Query("SELECT t FROM Terms t WHERE t.trmayrid = :ayrid AND t.trmname = :trmname")
    Terms findByTrmayridAndTrmname(@Param("ayrid") Long ayrid, @Param("trmname") String trmname);
}

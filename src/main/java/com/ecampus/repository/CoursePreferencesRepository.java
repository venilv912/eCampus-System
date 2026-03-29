package com.ecampus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecampus.model.*;

@Repository
public interface CoursePreferencesRepository extends JpaRepository<CoursePreferences, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ec2.coursepreferences crspref WHERE crspref.SID = :sid", nativeQuery = true)
    int deleteBySid(@Param("sid") Long sid);

    @Query(value = "SELECT COALESCE(MAX(crspref.cp_id), 0) FROM ec2.coursepreferences crspref", nativeQuery = true)
    Long findMaxId();

    @Query(value = "SELECT * FROM ec2.coursepreferences crspref WHERE crspref.SID = :sid", nativeQuery = true)
    List<CoursePreferences> getBySid(@Param("sid") Long sid);

    @Query(value = "SELECT st.stdinstid, cp.slot, crs.crscode, cp.pref_index FROM ec2.students st RIGHT JOIN ec2.coursepreferences cp ON st.stdid = cp.sid LEFT JOIN ec2.termcourses tc ON cp.tcrid = tc.tcrid LEFT JOIN ec2.courses crs ON tc.tcrcrsid = crs.crsid", nativeQuery = true)
    List<Object[]> getCrsPrefForElectiveRegistration();

}

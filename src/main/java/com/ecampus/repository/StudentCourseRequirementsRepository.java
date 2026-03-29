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
public interface StudentCourseRequirementsRepository extends JpaRepository<StudentCourseRequirements, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ec2.studentcourserequirements scr WHERE scr.SID = :sid", nativeQuery = true)
    int deleteBySid(@Param("sid") Long sid);

    @Query(value = "SELECT * FROM ec2.studentcourserequirements scr WHERE scr.SID = :sid", nativeQuery = true)
    List<StudentCourseRequirements> findBySid(@Param("sid") Long sid);

    @Query(value = "SELECT COALESCE(MAX(scr.scr_id), 0) FROM ec2.studentcourserequirements scr", nativeQuery = true)
    Long findMaxId();

    @Query(value = "SELECT scr.elect_type as elecType, scr.count as count FROM ec2.studentcourserequirements scr WHERE scr.SID = :sid", nativeQuery = true)
    List<Object[]> getBySid(@Param("sid") Long sid);

    @Query(value = "SELECT st.stdinstid, scr.elect_type, scr.count FROM ec2.students st RIGHT JOIN ec2.studentcourserequirements scr ON st.stdid = scr.sid", nativeQuery = true)
    List<Object[]> getStudReqForElectiveRegistration();

}

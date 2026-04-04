package com.ecampus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecampus.model.StudentRegistrationCourses;

@Repository
public interface StudentRegistrationCoursesRepository extends JpaRepository<StudentRegistrationCourses, Long> {

    List<StudentRegistrationCourses> findBySrcsrgidIn(List<Long> srgIds);

    Optional<StudentRegistrationCourses> findBySrcid(Long srcid);

    @Query(value = "SELECT COALESCE(MAX(src.srcid), 0) FROM ec2.studentregistrationcourses src", nativeQuery = true)
    Long findMaxSrcId();

    @Query(value = "select src.srctcrid from ec2.studentregistrationcourses src join ec2.coursetypes ct on src.orig_ctpid=ct.ctpid where src.srcsrgid=:srgid and ct.crscat='ELECTIVE'", nativeQuery = true)
    List<Long> findElectivesForStud(@Param("srgid") Long srgid);

    @Query(value = "select * from ec2.studentregistrationcourses src where src.srcsrgid=:srgid and src.srctcrid=:tcrid", nativeQuery = true)
    StudentRegistrationCourses findBySrgidTcrid(@Param("srgid") Long srgid, @Param("tcrid") Long tcrid);
}

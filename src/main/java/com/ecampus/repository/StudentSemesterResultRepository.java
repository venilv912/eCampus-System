package com.ecampus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Important for dynamic queries
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecampus.model.StudentSemesterResult;

// toupdate

@Repository
public interface StudentSemesterResultRepository extends
        JpaRepository<StudentSemesterResult, Long>,
        JpaSpecificationExecutor<StudentSemesterResult>, // Keep this for standard Specification-based queries that return StudentSemesterResult entities
        CustomStudentSemesterResultRepository // Extend your custom interface for the SPI/CPI list
{

    /**
     * Corresponds to: SELECT * FROM STUDENTSEMESTERRESULT WHERE SSRROWSTATE > 0 AND SSRSRGID = ?
     */
    List<StudentSemesterResult> findByStudentRegistration_SrgidAndSsrrowstateGreaterThan(Long registrationId, short rowState);
    @Query(value = "SELECT ssr.SSRCPI FROM ec2.STUDENTSEMESTERRESULT ssr WHERE ssr.SSRSRGID in (SELECT srg.SRGID FROM ec2.STUDENTREGISTRATIONS srg WHERE srg.SRGSTDID=:studentId) ORDER BY ssr.SSRSRGID DESC LIMIT 1", nativeQuery = true)
    String getcpi(@Param("studentId") Long studentId);

    @Query(value = "SELECT ssr.SSRCPI FROM ec2.STUDENTSEMESTERRESULT ssr WHERE ssr.SSRSRGID in (SELECT srg.SRGID FROM ec2.STUDENTREGISTRATIONS srg WHERE srg.SRGSTDID=:studentId) ORDER BY ssr.SSRSRGID DESC LIMIT 1 OFFSET 1", nativeQuery = true)
    String getlcpi(@Param("studentId") Long studentId);

    @Query(value = "SELECT * FROM ec2.STUDENTSEMESTERRESULT ssr WHERE ssr.SSRSRGID = :srgid", nativeQuery = true)
    StudentSemesterResult getBySrgid(@Param("srgid") Long srgid);
}
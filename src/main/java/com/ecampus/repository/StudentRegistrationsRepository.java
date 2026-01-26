package com.ecampus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecampus.model.StudentRegistrations;

@Repository
public interface StudentRegistrationsRepository extends JpaRepository<StudentRegistrations, Long> {

    @Query(value = "SELECT * FROM ec2.STUDENTREGISTRATIONS srg WHERE srg.SRGROWSTATE > 0 AND srg.SRGSTDID=:studentId", nativeQuery = true)
    List<StudentRegistrations> findregisteredsemesters(@Param("studentId") Long studentId);

    @Query(value = "SELECT * FROM ec2.STUDENTREGISTRATIONS srg WHERE srg.SRGROWSTATE > 0 AND srg.SRGSTDID=:studentId and srg.SRGSTRID=:semesterId", nativeQuery = true)
    StudentRegistrations getsrgbystdidandstrid(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    @Query("SELECT MAX(s.srgid) FROM StudentRegistrations s")
    Optional<Long> findMaxSrgid();
}

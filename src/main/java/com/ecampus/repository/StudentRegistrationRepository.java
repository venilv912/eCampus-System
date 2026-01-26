package com.ecampus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecampus.model.Semesters;
import com.ecampus.model.StudentRegistration;

@Repository
public interface StudentRegistrationRepository extends JpaRepository<StudentRegistration, Long> {

    // Get latest active registration record per student
    List<StudentRegistration> findBysrgstdidAndSrgrowstateGreaterThanOrderBySrgidDesc(
            Long studentId, int state);

    // Get most recent registration entry
    Optional<StudentRegistration> findTopBysrgstdidOrderBySrgidDesc(Long studentId);

    // Get all semesters registered by student in order
    @Query("SELECT sr FROM StudentRegistration sr JOIN sr.semesters s " +
            "WHERE sr.students.stdid = :studentId " +
            "ORDER BY s.strseqno ASC")
    List<StudentRegistration> findAllRegistrationsByStudentIdOrderBySemesterSequence(
            @Param("studentId") Long studentId);


    // Used for semester-wise grade card (corrected based on entity)
    @Query("SELECT r FROM StudentRegistration r " +
            "WHERE r.students.stdid = :studentId " +
            "AND r.semesters.strid = :semesterId " +
            "AND r.srgrowstate > 0")
    StudentRegistration findByStudentIdAndSemesterId(@Param("studentId") Long studentId,
                                                     @Param("semesterId") Long semesterId);


    // Semester dropdown loading
    @Query(value = "SELECT * FROM ec2.semesters WHERE strtrmid = :termId", nativeQuery = true)
    List<Semesters> findSemestersByTerm(@Param("termId") Long termId);
    @Query("""
       SELECT r
       FROM StudentRegistration r
       JOIN r.students s
       JOIN r.semesters sem
       WHERE sem.strid = :semesterId
         AND r.srgrowstate > 0
       ORDER BY s.stdinstid ASC
       """)
    List<StudentRegistration> findBySemesterOrderByStudentInstId(@Param("semesterId") Long semesterId);

}

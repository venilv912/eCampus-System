package com.ecampus.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecampus.model.Batches;
import com.ecampus.model.Students;

@Repository
public interface StudentsRepository extends JpaRepository<Students, Long> {

    // Native query for fetching one student by stdid
    @Query(value = "SELECT * FROM ec2.students st WHERE st.stdrowstate > 0 AND st.stdid = :studentId", nativeQuery = true)
    Students findStudent(@Param("studentId") Long studentId);

    // Latest stdid by institute id
    @Query(value = "SELECT st.stdid FROM ec2.students st WHERE st.stdinstid = :studentId ORDER BY st.stdrowstate DESC LIMIT 1", nativeQuery = true)
    Long findStdid(@Param("studentId") String studentId);

    // Batch id by institute id
    @Query(value = "SELECT st.stdbchid FROM ec2.students st WHERE st.stdinstid = :studentId ORDER BY st.stdrowstate DESC LIMIT 1", nativeQuery = true)
    Long findBatchIdByStudentId(@Param("studentId") String studentId);

    // Paging queries
    Page<Students> findByStdinstidAndStdrowstateGreaterThan(String stdinstid, int state, Pageable pageable);

    Page<Students> findByStdfirstnameContainingIgnoreCaseAndStdlastnameContainingIgnoreCaseAndStdrowstateGreaterThan(
            String fname, String lname, int state, Pageable pageable);

    Page<Students> findByStdfirstnameContainingIgnoreCaseAndStdrowstateGreaterThan(
            String fname, int state, Pageable pageable);

    Page<Students> findByStdlastnameContainingIgnoreCaseAndStdrowstateGreaterThan(
            String lname, int state, Pageable pageable);

    // Counts
    long countByStdfirstnameContainingIgnoreCaseAndStdrowstateGreaterThan(String stdfirstname, Long rowstate);

    long countByStdlastnameContainingIgnoreCaseAndStdrowstateGreaterThan(String stdlastname, Long rowstate);

    long countByStdfirstnameContainingIgnoreCaseAndStdlastnameContainingIgnoreCaseAndStdrowstateGreaterThan(
            String stdfirstname, String stdlastname, Long rowstate);

    // JPQL queries with StudentRegistrations will only work
    // if you actually mapped `@OneToMany` / `@ManyToOne` in Students.
    // Otherwise, keep them native.
    // Example fix (remove complex join if not mapped properly):
    @Query("SELECT s FROM Students s WHERE UPPER(s.stdfirstname) LIKE %:firstName% AND UPPER(s.stdlastname) LIKE %:lastName% AND s.stdrowstate > 0")
    List<Students> findStudentsByFullNameWithLatestRegistration(@Param("firstName") String firstName,
                                                                @Param("lastName") String lastName);

    @Query("SELECT s FROM Students s WHERE s.stdinstid = :stdinstid AND s.stdrowstate > 0")
    List<Students> findStudentByInstIdWithLatestRegistration(@Param("stdinstid") String stdinstid);

    // JPQL doesn’t allow LIMIT → Use native query
    @Query(value = "SELECT st.stdid FROM ec2.students st WHERE st.stdinstid = :stdinstid ORDER BY st.stdid ASC LIMIT 1", nativeQuery = true)
    List<Long> findStudentIdByInstituteId(@Param("stdinstid") String stdinstid);

    // Derived query by batch
    List<Students> findByBatch(Batches batches);
}

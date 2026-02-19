package com.ecampus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecampus.auth.user.UserDetailsRepository;
import com.ecampus.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>, UserDetailsRepository {

    List<Users> findByUrole0(String urole0);

    @Query(value = "SELECT * FROM ec2.users WHERE univid = :uname", nativeQuery = true)
    Optional<Users> findWithName(@Param("uname") String uname);

    @Query(value = "SELECT stdid FROM ec2.users WHERE uname = :username", nativeQuery = true)
    Long findIdByUname(@Param("username") String username);

    // Use native query so we hit the actual DB column `univid`
    @Query(value = "SELECT * FROM ec2.users WHERE univid = :univid", nativeQuery = true)
    Optional<Users> findByUnivId(@Param("univid") String univId);

    @Query(value = """
        SELECT u.*
        FROM ec2.users u
        JOIN ec2.ec2_roles r ON u.urole = r.rid
        WHERE u.univid = :univid
        """,
            nativeQuery = true)
    Optional<Users> findByUnividWithRoles(@Param("univid") String univId);

    // All faculty (urole = '913', row_state > 0) without pagination
    @Query(value = """
        SELECT * FROM ec2.users
        WHERE urole = '913'
        AND ufullname is not null
        AND row_state > 0
        ORDER BY ufullname
        """,
            nativeQuery = true)
    List<Users> findAllFacultyList();

    // Full-text search among faculty (urole = '913', row_state > 0) without pagination
    @Query(value = """
        SELECT * FROM ec2.users
        WHERE urole = '913'
          AND row_state > 0
          AND to_tsvector('english',
                coalesce(uname, '') || ' ' ||
                coalesce(ufullname, '') || ' ' ||
                coalesce(uemail, '')
              ) @@ plainto_tsquery('english', :keyword)
        ORDER BY ufullname
        """,
            nativeQuery = true)
    List<Users> searchFacultyList(@Param("keyword") String keyword);

    @Query(value = """
        SELECT *
        FROM ec2.users
        WHERE urole_0 = 'FACULTY'
          AND LOWER(uemail) LIKE LOWER(CONCAT('%', :query, '%'))
        """,
            nativeQuery = true)
    List<Users> searchFacultyByName(@Param("query") String query);

    List<Users> findByUemailContainingIgnoreCase(String name);
}

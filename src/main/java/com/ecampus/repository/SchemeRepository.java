package com.ecampus.repository;

import com.ecampus.model.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SchemeRepository extends JpaRepository<Scheme, Long> {

    // This is the method to add:
    List<Scheme> findByProgram_Prgid(Long programId);

}

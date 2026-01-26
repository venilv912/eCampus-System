package com.ecampus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecampus.model.Semesters;
import com.ecampus.model.StudentRegistrations;
import com.ecampus.model.Students;
import com.ecampus.repository.SemestersRepository;
import com.ecampus.repository.StudentRegistrationsRepository;
import com.ecampus.repository.StudentsRepository;

@Service
public class StudentRegistrationService {
    @Autowired
    private StudentsRepository studentRepo;

    @Autowired
    private SemestersRepository semesterRepo;

    @Autowired
    private StudentRegistrationsRepository registrationRepo;


    public Students getStudentById(Long id) {
        return studentRepo.findStudent(id);
    }

    public List<Semesters> getSemesterById(Long id) {
        return semesterRepo.findActiveSemestersByBranchId(id);
    }

    public Long getMaxSemesterId(Long batchId) {
        return semesterRepo.findMaxSemesterId(batchId);
    }
    public List<StudentRegistrations> getRegistrationsByStudentId(Long studentId) {
        return registrationRepo.findregisteredsemesters(studentId);
    }

}

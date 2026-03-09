package com.ecampus.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecampus.model.*;
import com.ecampus.repository.*;
@Service
public class SpecifyOpenForService {
    
    @Autowired
    private SchemeDetailsRepository SchemeDetailsRepo;

    @Autowired
    private BatchesRepository BatchesRepo;

    @Autowired
    private TermCourseAvailableForRepository TermCourseAvailableForRepo;

    @Autowired
    private TermCoursesRepository TermCoursesRepo;

    @Autowired
    private CourseTypesRepository CourseTypesRepo;

    @Autowired
    private TermsRepository TermsRepo;

    @Autowired
    private AcademicYearsRepository AcademicYearsRepo;

    public Long getSchemeIdFromSplName(String SplName){
        return SchemeDetailsRepo.getSchIdFromSpl(SplName);
    }

    public Long getSplIdFromSplName(String SplName){
        return SchemeDetailsRepo.getSplIdFromSpl(SplName);
    }

    public Long getBchIdFromBchSchSpl(String BchName, Long SchId, Long SplId){
        return BatchesRepo.getIdFromBchSchSpl(BchName, SchId, SplId);
    }

    public List<TermCourseAvailableFor> getCrsForBch(Long termId, Long BchId){
        return TermCourseAvailableForRepo.getCrsByBch(termId,BchId);
    }

    @Transactional
    public void addTCAfromTCR(Long termId, Long BatchId){
        List<Long> termCourseIds = TermCoursesRepo.getElectiveIdByTrmId(termId);

        Long currtcaId = TermCourseAvailableForRepo.findMaxTcaid();
        for(Long tcid : termCourseIds){
            currtcaId++;
            TermCourseAvailableFor TCAFor = new TermCourseAvailableFor();
            TCAFor.setTcaid(currtcaId);
            TCAFor.setTcatcrid(tcid);
            TCAFor.setTcaprgid(0L);
            TCAFor.setTcastatus("T");
            TCAFor.setTcacreatedby(1150L);
            TCAFor.setTcacreatedat(LocalDateTime.now());
            TCAFor.setTcalastupdatedby(null);
            TCAFor.setTcalastupdatedat(null);
            TCAFor.setTcarowstate(1L);
            TCAFor.setTcabchid(BatchId);
            TCAFor.setTcaelectivetype(null);
            TCAFor.setTca_seats(0L);
            TCAFor.setTca_booked(0L);

            TermCourseAvailableForRepo.save(TCAFor);
        }
    }

    public Long getSemNoByBchTrm(String batch, Long termId){
        String acadYear = AcademicYearsRepo.getYrByTrmId(termId);  // Eg. 2022-2023
        String term = TermsRepo.getTrmById(termId);                // Autumn or Winter

        Long acadStartYear = Long.parseLong(acadYear.split("-")[0]);
        Long batchYear = Long.parseLong(batch);

        Long semNo = (acadStartYear - batchYear)*2;
        return term=="Autumn" ? semNo+1 : semNo+2;
    }

    public List<Object[]> getTypeElectBySchSplSem(Long schemeId, Long splId, Long semNo){
        return CourseTypesRepo.getElectiveTypeBySchSplSem(schemeId, splId, semNo);
    }

    @Transactional
    public void updateElectiveType(List<Long> selectedTcaIds, String electiveCode){
        if (selectedTcaIds == null || selectedTcaIds.isEmpty()) {
            return;
        }

        for(Long currTcaId : selectedTcaIds){
            TermCourseAvailableFor TCA = TermCourseAvailableForRepo.findById(currTcaId)
                .orElseThrow(() -> new RuntimeException("Course record not found for ID: " + currTcaId));

            TCA.setTcalastupdatedby(1150L);
            TCA.setTcalastupdatedat(LocalDateTime.now());
            TCA.setTcaelectivetype(electiveCode);
            TCA.setTca_seats(0L);
            TCA.setTca_booked(0L);

            TermCourseAvailableForRepo.save(TCA);
        }
    }

    @Transactional
    public void updateTcaSeats(Long tcaId, Long seats) {
        TermCourseAvailableFor tca = TermCourseAvailableForRepo.findById(tcaId).orElse(null);
        if (tca != null) {
            tca.setTca_seats(seats);
            TermCourseAvailableForRepo.save(tca);
        }
    }

}

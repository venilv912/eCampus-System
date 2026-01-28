package com.ecampus.controller.admin;

import com.ecampus.dto.BatchViewDTO;
import com.ecampus.model.Batches;
import com.ecampus.model.AcademicYears;
import com.ecampus.model.Programs;
import com.ecampus.model.Scheme;
import com.ecampus.repository.BatchesRepository;
import com.ecampus.repository.AcademicYearsRepository;
import com.ecampus.repository.ProgramsRepository;
import com.ecampus.repository.SchemeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/batches")
public class BatchesController {

    @Autowired
    private BatchesRepository batchRepository;

    @Autowired
    private AcademicYearsRepository academicYearRepository;

    @Autowired
    private SchemeRepository schemeRepository;
    @Autowired
    private ProgramsRepository programsRepository;
    // Show all batches
    @GetMapping
    public String listBatches(Model model) {
        List<Object[]> rows = batchRepository.getAllBatchesDetailsRaw();

        List<BatchViewDTO> batches = rows.stream()
                .map(r -> new BatchViewDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        ((Number) r[4]).intValue(),
                        r[5] != null ? ((Number) r[5]).intValue() : null
                )).toList();

        model.addAttribute("batches", batches);
        return "admin/batches";
    }

    // Show Add Batch Page
    @GetMapping("/add")
    public String showAddBatch(Model model) {

        model.addAttribute("batch", new Batches());

        List<AcademicYears> years = academicYearRepository.findAllByOrderByAyridDesc();
        List<Scheme> schemes = schemeRepository.findAll();
        List<Programs>programs= programsRepository.findAll();
        model.addAttribute("years", years);
        model.addAttribute("schemes", schemes);
        model.addAttribute("programs",programs);

        return "admin/batch-form";
    }

    // Handle Save
    @PostMapping("/add")
    public String saveBatch(@ModelAttribute("batch") Batches batch) {

        // Auto-increment ID
        Integer maxId = batchRepository.findMaxBatchId();
        batch.setBchid(maxId == null ? 1 : (long) (maxId + 1));

        // SET PROGRAMS for batch
        Programs program = programsRepository
                .findById(batch.getBchprgid())
                .orElseThrow(() -> new RuntimeException("Program not found"));

        batch.setPrograms(program);

        // AUTO-FILL REQUIRED SYSTEM FIELDS
        batch.setBchcreatedat(LocalDateTime.now());
        batch.setBchlastupdatedat(LocalDateTime.now());
        batch.setBchcreatedby(0L);         // Or logged-in user ID
        batch.setBchlastupdatedby(0L);     // Or logged-in user ID
        batch.setBchrowstate(0L);               // Active or your default
        batch.setBchinstcode("2021");           // Whatever your default is
        batch.setBchfield1(0L);                  // If NOT NULL, set empty string

        batchRepository.save(batch);
        return "redirect:/admin/batches";
    }

}

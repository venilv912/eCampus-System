package com.ecampus.controller.admin;

import com.ecampus.dto.BatchViewDTO;
import com.ecampus.model.Batches;
import com.ecampus.model.AcademicYears;
import com.ecampus.model.Programs;
import com.ecampus.model.Scheme;
import com.ecampus.model.SchemeDetails;
import com.ecampus.repository.BatchesRepository;
import com.ecampus.repository.AcademicYearsRepository;
import com.ecampus.repository.ProgramsRepository;
import com.ecampus.repository.SchemeRepository;
import com.ecampus.repository.SchemeDetailsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private SchemeDetailsRepository schemeDetailsRepository;
    
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
        List<Programs> programs = programsRepository.findAll();
        model.addAttribute("years", years);
        model.addAttribute("programs", programs);

        return "admin/batch-form";
    }
    
    // AJAX endpoint: Get schemes by program ID
    @GetMapping("/api/schemes/{programId}")
    @ResponseBody
    public List<Scheme> getSchemesByProgram(@PathVariable Long programId) {
        return schemeRepository.findByProgram_Prgid(programId);
    }
    
    // AJAX endpoint: Get specializations by scheme ID
    @GetMapping("/api/specializations/{schemeId}")
    @ResponseBody
    public List<SchemeDetails> getSpecializationsByScheme(@PathVariable Long schemeId) {
        return schemeDetailsRepository.findBySchemeId(schemeId);
    }

    // Show Edit Batch Page
    @GetMapping("/edit/{id}")
    public String showEditBatch(@PathVariable Long id, Model model) {
        Batches batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        
        // Get display values for disabled fields
        List<Object[]> rows = batchRepository.getAllBatchesDetailsRaw();
        BatchViewDTO batchView = rows.stream()
                .map(r -> new BatchViewDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        (String) r[3],
                        ((Number) r[4]).intValue(),
                        r[5] != null ? ((Number) r[5]).intValue() : null
                ))
                .filter(b -> b.bchid().equals(id))
                .findFirst()
                .orElse(null);
        
        model.addAttribute("batch", batch);
        model.addAttribute("batchView", batchView);
        return "admin/batch-edit-form";
    }
    
    // Handle Update
    @PostMapping("/edit/{id}")
    public String updateBatch(@PathVariable Long id, @ModelAttribute("batch") Batches batchForm) {
        Batches existingBatch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        
        // Only update editable fields
        existingBatch.setBchname(batchForm.getBchname());
        existingBatch.setBchcapacity(batchForm.getBchcapacity());
        existingBatch.setBchlastupdatedat(LocalDateTime.now());
        
        batchRepository.save(existingBatch);
        return "redirect:/admin/batches";
    }

    // Handle Save
    @PostMapping("/add")
    public String saveBatch(@ModelAttribute("batch") Batches batch, RedirectAttributes redirectAttributes) {

        // Check for duplicate batch
        boolean exists = batchRepository.existsByBchprgidAndSchemeIdAndSplidAndBchcalid(
                batch.getBchprgid(), batch.getSchemeId(), batch.getSplid(), batch.getBchcalid());
        
        if (exists) {
            redirectAttributes.addFlashAttribute("error", "Batch already exists.");
            return "redirect:/admin/batches/add";
        }

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

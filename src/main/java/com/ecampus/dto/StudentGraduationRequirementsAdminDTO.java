package com.ecampus.dto;

import java.math.BigDecimal;

/**
 * DTO for admin page displaying graduation requirements status of all students in a batch.
 * Contains aggregated data (total courses, credits, types fulfilled) and eligibility status.
 */
public class StudentGraduationRequirementsAdminDTO {

    private Long stdid;
    private String stdinstid;
    private String studentName;
    private Long coursesCompleted;
    private Long minRequiredCourses;
    private BigDecimal creditsEarned;
    private BigDecimal minRequiredCredits;
    private Long typesFulfilled;
    private Long totalTypes;
    private BigDecimal cpi;
    private BigDecimal minRequiredCpi;
    private String graduationStatus; // "Completed" or "Incomplete"
    private String statusColor; // "success" or "warning"

    public StudentGraduationRequirementsAdminDTO(
            Long stdid,
            String stdinstid,
            String studentName,
            Long coursesCompleted,
            Long minRequiredCourses,
            BigDecimal creditsEarned,
            BigDecimal minRequiredCredits,
            Long typesFulfilled,
            Long totalTypes,
            BigDecimal cpi,
            BigDecimal minRequiredCpi,
            String graduationStatus,
            String statusColor) {
        this.stdid = stdid;
        this.stdinstid = stdinstid;
        this.studentName = studentName;
        this.coursesCompleted = coursesCompleted;
        this.minRequiredCourses = minRequiredCourses;
        this.creditsEarned = creditsEarned;
        this.minRequiredCredits = minRequiredCredits;
        this.typesFulfilled = typesFulfilled;
        this.totalTypes = totalTypes;
        this.cpi = cpi;
        this.minRequiredCpi = minRequiredCpi;
        this.graduationStatus = graduationStatus;
        this.statusColor = statusColor;
    }

    // Getters
    public Long getStdid() { return stdid; }
    public String getStdinstid() { return stdinstid; }
    public String getStudentName() { return studentName; }
    public Long getCoursesCompleted() { return coursesCompleted; }
    public Long getMinRequiredCourses() { return minRequiredCourses; }
    public BigDecimal getCreditsEarned() { return creditsEarned; }
    public BigDecimal getMinRequiredCredits() { return minRequiredCredits; }
    public Long getTypesFulfilled() { return typesFulfilled; }
    public Long getTotalTypes() { return totalTypes; }
    public BigDecimal getCpi() { return cpi; }
    public BigDecimal getMinRequiredCpi() { return minRequiredCpi; }
    public String getGraduationStatus() { return graduationStatus; }
    public String getStatusColor() { return statusColor; }
}

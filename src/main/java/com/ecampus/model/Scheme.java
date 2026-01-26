package com.ecampus.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Scheme")
public class Scheme {

    @Id
    @Column(name = "scheme_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Programs program;
    @Column(name = "effective_from_year", nullable = false)
    private Long effectiveFromYear;

    @Column(name = "max_credit_load")
    private Long maxCreditLoad;

    @Column(name = "max_courses")
    private Long maxCourses;

    @Column(name = "min_cpi", precision = 4, scale = 2, nullable = false)
    private Long minCpi;

    @Column(name = "min_credits", nullable = false)
    private Long minCredits;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Programs getProgram() {
        return program;
    }

    public void setProgram(Programs program) {
        this.program = program;
    }

    public Long getEffectiveFromYear() {
        return effectiveFromYear;
    }

    public void setEffectiveFromYear(Long effectiveFromYear) {
        this.effectiveFromYear = effectiveFromYear;
    }

    public Long getMaxCreditLoad() {
        return maxCreditLoad;
    }

    public void setMaxCreditLoad(Long maxCreditLoad) {
        this.maxCreditLoad = maxCreditLoad;
    }

    public Long getMaxCourses() {
        return maxCourses;
    }

    public void setMaxCourses(Long maxCourses) {
        this.maxCourses = maxCourses;
    }

    public Long getMinCpi() {
        return minCpi;
    }

    public void setMinCpi(Long minCpi) {
        this.minCpi = minCpi;
    }

    public Long getMinCredits() {
        return minCredits;
    }

    public void setMinCredits(Long minCredits) {
        this.minCredits = minCredits;
    }
}

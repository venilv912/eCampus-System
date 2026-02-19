package com.ecampus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "scheme", schema = "ec2")
public class Scheme {

    @Id
    @Column(name = "scheme_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    @JsonIgnore
    private Programs program;

    @Column(name = "effective_from_year", nullable = false)
    private Long effectiveFromYear;

    // ---- getters & setters ----

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
}

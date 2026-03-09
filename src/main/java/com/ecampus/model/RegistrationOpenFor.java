package com.ecampus.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "registrationopenfor", schema = "ec2")
@IdClass(RegistrationOpenForId.class)
public class RegistrationOpenFor {

    /* ========================
       Composite Primary Key
       ======================== */

    @Id
    @Column(name = "termid", nullable = false)
    private Long termid;

    @Id
    @Column(name = "batchid", nullable = false)
    private Long batchid;

    @Id
    @Column(name = "registrationtype", nullable = false)
    private String registrationtype;

    /* ========================
       Columns
       ======================== */

    @Column(name = "startdate", nullable = false)
    private LocalDateTime startdate;

    @Column(name = "enddate", nullable = false)
    private LocalDateTime enddate;

    /* ========================
       Relationship
       ======================== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "termid",
        referencedColumnName = "trmid",
        insertable = false,
        updatable = false
    )
    private Terms term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "batchid",
        referencedColumnName = "bchid",
        insertable = false,
        updatable = false
    )
    private Batches batch;

    /* ========================
       Getters & Setters
       ======================== */

    public Long getTermid() {
        return termid;
    }

    public void setTermid(Long termid) {
        this.termid = termid;
    }

    public Long getBatchid() {
        return batchid;
    }

    public void setBatchid(Long batchid) {
        this.batchid = batchid;
    }

    public String getRegistrationtype() {
        return registrationtype;
    }

    public void setRegistrationtype(String registrationtype) {
        this.registrationtype = registrationtype;
    }

    public LocalDateTime getStartdate() {
        return startdate;
    }

    public void setStartdate(LocalDateTime startdate) {
        this.startdate = startdate;
    }

    public LocalDateTime getEnddate() {
        return enddate;
    }

    public void setEnddate(LocalDateTime enddate) {
        this.enddate = enddate;
    }

    public Terms getTerm() {
        return term;
    }

    public Batches getBatch() {
        return batch;
    }
}
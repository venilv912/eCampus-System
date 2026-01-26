package com.ecampus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "terms", schema="ec2")
public class Terms {

    @Id
    @Column(name = "trmid")
    private Long trmid;

    @Column(name = "trmname")
    private String trmname;

    @Column(name = "trmseqno")
    private Long trmseqno;

    @Column(name = "trm_starts")
    private LocalDate trmStarts;

    @Column(name = "trm_ends")
    private LocalDate trmEnds;

    @Column(name = "trmcreatedby")
    private Long trmcreatedby;

    @Column(name = "trmcreatedat")
    private LocalDateTime trmcreatedat;

    @Column(name = "trmlastupdatedby")
    private Long trmlastupdatedby;

    @Column(name = "trmlastupdatedat")
    private LocalDateTime trmlastupdatedat;

    @Column(name = "trmrowstate")
    private Long trmrowstate;

    @Column(name = "trmayrid", insertable=false, updatable=false)
    private Long trmayrid;

    @ManyToOne
    @JoinColumn(name = "trmayrid", referencedColumnName = "ayrid", nullable = false)
    @JsonIgnore
    private AcademicYears academicYear;

    // Getters and setters
    public Long getTrmid() { return trmid; }
    public void setTrmid(Long trmid) { this.trmid = trmid; }

    public Long getTrmayrid() { return trmayrid; }
    public void setTrmayrid(Long trmayrid) { this.trmayrid = trmayrid; }

    public String getTrmname() { return trmname; }
    public void setTrmname(String trmname) { this.trmname = trmname; }

    public Long getTrmseqno() { return trmseqno; }
    public void setTrmseqno(Long trmseqno) { this.trmseqno = trmseqno; }

    public LocalDate getTrmstarts() { return trmStarts; }
    public void setTrmstarts(LocalDate trmStarts) { this.trmStarts = trmStarts; }

    public LocalDate getTrmends() { return trmEnds; }
    public void setTrmends(LocalDate trmEnds) { this.trmEnds = trmEnds; }

    public Long getTrmcreatedby() { return trmcreatedby; }
    public void setTrmcreatedby(Long trmcreatedby) { this.trmcreatedby = trmcreatedby; }

    public LocalDateTime getTrmcreatedat() { return trmcreatedat; }
    public void setTrmcreatedat(LocalDateTime trmcreatedat) { this.trmcreatedat = trmcreatedat; }

    public Long getTrmlastupdatedby() { return trmlastupdatedby; }
    public void setTrmlastupdatedby(Long trmlastupdatedby) { this.trmlastupdatedby = trmlastupdatedby; }

    public LocalDateTime getTrmlastupdatedat() { return trmlastupdatedat; }
    public void setTrmlastupdatedat(LocalDateTime trmlastupdatedat) { this.trmlastupdatedat = trmlastupdatedat; }

    public Long getTrmrowstate() { return trmrowstate; }
    public void setTrmrowstate(Long trmrowstate) { this.trmrowstate = trmrowstate; }

    public AcademicYears getAcademicYear() { return academicYear; }
    public void setAcademicYear(AcademicYears academicYear) { this.academicYear = academicYear; }
}

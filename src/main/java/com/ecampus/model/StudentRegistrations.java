package com.ecampus.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "studentregistrations", schema="ec2")
public class StudentRegistrations {

    @Id
    @Column(name = "srgid")
    private Long srgid;

    @Column(name = "srgregdate")
    private LocalDate srgregdate;

    @Column(name = "srgfield1")
    private String srgfield1;

    @Column(name = "srgfield2")
    private String srgfield2;

    @Column(name = "srgcreatedby")
    private Long srgcreatedby;

    @Column(name = "srgcreatedat")
    private LocalDateTime srgcreatedat;

    @Column(name = "srglastupdatedby")
    private Long srglastupdatedby;

    @Column(name = "srglastupdatedat")
    private LocalDateTime srglastupdatedat;

    @Column(name = "srgrowstate")
    private Long srgrowstate;

    @Column(name = "srgstrid", insertable = false, updatable = false)
    private Long srgstrid;

    @Column(name = "srgstdid", insertable = false, updatable = false)
    private Long srgstdid;

    @ManyToOne
    @JoinColumn(name = "srgstdid", nullable = false)
    private Students students;

    public Students getStudents() {
        return students;
    }
    public void setStudents(Students students) {
        this.students = students;
    }
    @ManyToOne
    @JoinColumn(name = "srgstrid", nullable = false)
    private Semesters semesters;

    public Semesters getSemester() {
        return semesters;
    }
    public void setSemester(Semesters semester) {
        this.semesters = semester;
    }
    public Long getSrgid() { return srgid; }
    public void setSrgid(Long srgid) { this.srgid = srgid; }

    public Long getSrgstdid() { return srgstdid; }
    public void setSrgstdid(Long srgstdid) { this.srgstdid = srgstdid; }

    public Long getSrgstrid() { return srgstrid; }
    public void setSrgstrid(Long srgstrid) { this.srgstrid = srgstrid; }

    public LocalDate getSrgregdate() { return srgregdate; }
    public void setSrgregdate(LocalDate srgregdate) { this.srgregdate = srgregdate; }

    public String getSrgfield1() { return srgfield1; }
    public void setSrgfield1(String srgfield1) { this.srgfield1 = srgfield1; }

    public String getSrgfield2() { return srgfield2; }
    public void setSrgfield2(String srgfield2) { this.srgfield2 = srgfield2; }

    public Long getSrgcreatedby() { return srgcreatedby; }
    public void setSrgcreatedby(Long srgcreatedby) { this.srgcreatedby = srgcreatedby; }

    public LocalDateTime getSrgcreatedat() { return srgcreatedat; }
    public void setSrgcreatedat(LocalDateTime srgcreatedat) { this.srgcreatedat = srgcreatedat; }

    public Long getSrglastupdatedby() { return srglastupdatedby; }
    public void setSrglastupdatedby(Long srglastupdatedby) { this.srglastupdatedby = srglastupdatedby; }

    public LocalDateTime getSrglastupdatedat() { return srglastupdatedat; }
    public void setSrglastupdatedat(LocalDateTime srglastupdatedat) { this.srglastupdatedat = srglastupdatedat; }

    public Long getSrgrowstate() { return srgrowstate; }
    public void setSrgrowstate(Long srgrowstate) { this.srgrowstate = srgrowstate; }

}

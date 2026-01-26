package com.ecampus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "studentregistrations",schema="ec2")
public class StudentRegistration {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long srgid;
    private int srgrowstate;

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
    public Long getSrgid() {
        return srgid;
    }
    public void setSrgid(Long srgid) {
        this.srgid = srgid;
    }
    public Long getSrgstdid() {
        return srgstdid;
    }
    public void setSrgstdid(Long srgstdid) {
        this.srgstdid = srgstdid;
    }
    public Long getSrgstrid() {
        return srgstrid;
    }
    public void setSrgstrid(Long srgstrid) {
        this.srgstrid = srgstrid;
    }
    public int getSrgrowstate() {
        return srgrowstate;
    }
    public void setSrgrowstate(int srgrowstate) {
        this.srgrowstate = srgrowstate;
    }


}


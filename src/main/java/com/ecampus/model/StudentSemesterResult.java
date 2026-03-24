package com.ecampus.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// toupdate

@Entity
@Table(name = "studentsemesterresult", schema = "ec2")
public class StudentSemesterResult {

    @Id
    @Column(name = "ssrid")
    private Long ssrid;

    @Column(name = "ssrsrgid")
    private Long ssrsrgid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ssrsrgid", insertable = false, updatable = false)
    private StudentRegistrations studentRegistration;

    @Column(name = "ssrcreditsregistered", nullable = false, precision = 38, scale = 2)
    private BigDecimal ssrcreditsregistered;

    @Column(name = "ssrcreditsearned", nullable = false, precision = 38, scale = 2)
    private BigDecimal ssrcreditsearned;

    @Column(name = "ssrgradepointsearned", nullable = false, precision = 38, scale = 2)
    private BigDecimal ssrgradepointsearned;

    @Column(name = "ssrcumcreditsregistered", nullable = false, precision = 38, scale = 2)
    private BigDecimal ssrcumcreditsregistered;

    @Column(name = "ssrcumcreditsearned", nullable = false, precision = 38, scale = 2)
    private BigDecimal ssrcumcreditsearned;

    @Column(name = "ssrcumgradepointsearned", nullable = false, precision = 38, scale = 2)
    private BigDecimal ssrcumgradepointsearned;

    @Column(name = "ssrspigradepoint", nullable = false, precision = 38, scale = 2)
    private BigDecimal ssrspigradepoint;

    @Column(name = "ssrregisteredcredits", nullable = false, precision = 9, scale = 2)
    private BigDecimal ssrregisteredcredits;

    @Column(name = "ssrcpigradepoints", precision = 38, scale = 2)
    private BigDecimal ssrcpigradepoints;

    @Column(name = "ssrcpiregisteredcredits", precision = 38, scale = 2)
    private BigDecimal ssrcpiregisteredcredits;

    @Column(name = "ssrspi", length = 255)
    private String ssrspi;

    @Column(name = "ssrcpi", length = 255)
    private String ssrcpi;

    @Column(name = "ssrfield1", precision = 10, scale = 2)
    private BigDecimal ssrfield1;

    @Column(name = "ssrfield2", precision = 10, scale = 2)
    private BigDecimal ssrfield2;

    @Column(name = "ssrfield3", precision = 38, scale = 2)
    private BigDecimal ssrfield3;

    @Column(name = "ssrcreatedby")
    private Long ssrcreatedby;

    @Column(name = "ssrcreatedat")
    private LocalDateTime ssrcreatedat;

    @Column(name = "ssrlastupdatedby")
    private Long ssrlastupdatedby;

    @Column(name = "ssrlastupdatedat")
    private LocalDateTime ssrlastupdatedat;

    @Column(name = "ssrrowstate")
    private Long ssrrowstate;

    @Column(name = "ssrcpi_numeric")
    private BigDecimal ssrcpiNumeric;

    @Column(name = "ssrspi_numeric")
    private BigDecimal ssrspiNumeric;

    @Column(name = "grade")
    private Long grade;


    // Getters and Setters

    public Long getSsrid() {return ssrid;}
    public void setSsrid(Long ssrid) {this.ssrid = ssrid;}

    public Long getSsrsrgid() {return ssrsrgid;}
    public void setSsrsrgid(Long ssrsrgid) {this.ssrsrgid = ssrsrgid;}

    public StudentRegistrations getStudentRegistration() {return studentRegistration;}
    public void setStudentRegistration(StudentRegistrations studentRegistration) {this.studentRegistration = studentRegistration;}

    public BigDecimal getSsrcreditsregistered() {return ssrcreditsregistered;}
    public void setSsrcreditsregistered(BigDecimal ssrcreditsregistered) {this.ssrcreditsregistered = ssrcreditsregistered;}
    
    public BigDecimal getSsrcreditsearned() {return ssrcreditsearned;}
    public void setSsrcreditsearned(BigDecimal ssrcreditsearned) {this.ssrcreditsearned = ssrcreditsearned;}

    public BigDecimal getSsrgradepointsearned() {return ssrgradepointsearned;}
    public void setSsrgradepointsearned(BigDecimal ssrgradepointsearned) {this.ssrgradepointsearned = ssrgradepointsearned;}

    public BigDecimal getSsrcumcreditsregistered() {return ssrcumcreditsregistered;}
    public void setSsrcumcreditsregistered(BigDecimal ssrcumcreditsregistered) {this.ssrcumcreditsregistered = ssrcumcreditsregistered;}

    public BigDecimal getSsrcumcreditsearned() {return ssrcumcreditsearned;}
    public void setSsrcumcreditsearned(BigDecimal ssrcumcreditsearned) {this.ssrcumcreditsearned = ssrcumcreditsearned;}

    public BigDecimal getSsrcumgradepointsearned() {return ssrcumgradepointsearned;}
    public void setSsrcumgradepointsearned(BigDecimal ssrcumgradepointsearned) {this.ssrcumgradepointsearned = ssrcumgradepointsearned;}

    public BigDecimal getSsrspigradepoint() {return ssrspigradepoint;}
    public void setSsrspigradepoint(BigDecimal ssrspigradepoint) {this.ssrspigradepoint = ssrspigradepoint;}

    public BigDecimal getSsrregisteredcredits() {return ssrregisteredcredits;}
    public void setSsrregisteredcredits(BigDecimal ssrregisteredcredits) {this.ssrregisteredcredits = ssrregisteredcredits;}

    public BigDecimal getSsrcpigradepoints() {return ssrcpigradepoints;}
    public void setSsrcpigradepoints(BigDecimal ssrcpigradepoints) {this.ssrcpigradepoints = ssrcpigradepoints;}

    public BigDecimal getSsrcpiregisteredcredits() {return ssrcpiregisteredcredits;}
    public void setSsrcpiregisteredcredits(BigDecimal ssrcpiregisteredcredits) {this.ssrcpiregisteredcredits = ssrcpiregisteredcredits;}

    public String getSsrspi() {return ssrspi;}
    public void setSsrspi(String ssrspi) {this.ssrspi = ssrspi;}

    public String getSsrcpi() {return ssrcpi;}
    public void setSsrcpi(String ssrcpi) {this.ssrcpi = ssrcpi;}

    public BigDecimal getSsrfield1() {return ssrfield1;}
    public void setSsrfield1(BigDecimal ssrfield1) {this.ssrfield1 = ssrfield1;}

    public BigDecimal getSsrfield2() {return ssrfield2;}
    public void setSsrfield2(BigDecimal ssrfield2) {this.ssrfield2 = ssrfield2;}

    public BigDecimal getSsrfield3() {return ssrfield3;}
    public void setSsrfield3(BigDecimal ssrfield3) {this.ssrfield3 = ssrfield3;}

    public Long getSsrcreatedby() {return ssrcreatedby;}
    public void setSsrcreatedby(Long ssrcreatedby) {this.ssrcreatedby = ssrcreatedby;}

    public LocalDateTime getSsrcreatedat() {return ssrcreatedat;}
    public void setSsrcreatedat(LocalDateTime ssrcreatedat) {this.ssrcreatedat = ssrcreatedat;}

    public Long getSsrlastupdatedby() {return ssrlastupdatedby;}
    public void setSsrlastupdatedby(Long ssrlastupdatedby) {this.ssrlastupdatedby = ssrlastupdatedby;}

    public LocalDateTime getSsrlastupdatedat() {return ssrlastupdatedat;}
    public void setSsrlastupdatedat(LocalDateTime ssrlastupdatedat) {this.ssrlastupdatedat = ssrlastupdatedat;}

    public Long getSsrrowstate() {return ssrrowstate;}
    public void setSsrrowstate(Long ssrrowstate) {this.ssrrowstate = ssrrowstate;}

    public BigDecimal getSsrcpiNumeric() {return ssrcpiNumeric;}
    public void setSsrcpiNumeric(BigDecimal ssrcpiNumeric) {this.ssrcpiNumeric = ssrcpiNumeric;}

    public BigDecimal getSsrspiNumeric() {return ssrspiNumeric;}
    public void setSsrspiNumeric(BigDecimal ssrspiNumeric) {this.ssrspiNumeric = ssrspiNumeric;}

    public Long getGrade() {return grade;}
    public void setGrade(Long grade) {this.grade = grade;}
}
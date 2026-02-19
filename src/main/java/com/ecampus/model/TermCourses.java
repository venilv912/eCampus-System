package com.ecampus.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "termcourses", schema = "ec2")
public class TermCourses {

    @Id
    @Column(name = "tcrid")
    private Long tcrid;

    @Column(name = "tcrtrmid")
    private Long tcrtrmid;

    @Column(name = "tcrcrsid")
    private Long tcrcrsid;

    @Column(name = "tcrtype")
    private String tcrtype = "REGULAR";

    @Column(name = "tcrfacultyid")
    private Long tcrfacultyid;

    @Column(name = "tcrroundlogic")
    private String tcrroundlogic = "ROUND";

    @Column(name = "tcrmarks")
    private Long tcrmarks = 100L;

    @Column(name = "tcrstatus")
    private String tcrstatus = "AVAILABLE";

    @Column(name = "tcr_access_status")
    private String tcrAccessStatus;

    @Column(name = "tcrcreatedby")
    private Long tcrcreatedby = 0L;

    @Column(name = "tcrcreatedat")
    private LocalDateTime tcrcreatedat = LocalDateTime.now();

    @Column(name = "tcrlastupdatedby")
    private Long tcrlastupdatedby;

    @Column(name = "tcrlastupdatedat")
    private LocalDateTime tcrlastupdatedat;

    @Column(name = "tcrrowstate")
    private Long tcrrowstate = 1L;

//    @Column(name = "slot")
//    private String slot;

    @Column(name = "tcrslot")
    private Long tcrslot;

    @Column(name = "crstype")
    private String crstype; // Course Type ("Core", "Elective", "IP") (IP=Internship/Project)

    @ManyToOne
    @JoinColumn(name = "tcrcrsid", referencedColumnName = "crsid", insertable = false, updatable = false)
    private Courses course;

    @ManyToOne
    @JoinColumn(name = "tcrfacultyid", referencedColumnName = "uid", insertable = false, updatable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "tcrtrmid",referencedColumnName= "trmid", insertable = false, updatable = false)
    private Terms term;

    // Automatically fill defaults before inserting into DB
    @PrePersist
    public void prePersist() {
        if (this.tcrtype == null) this.tcrtype = "REGULAR";
        if (this.tcrroundlogic == null) this.tcrroundlogic = "ROUND";
        if (this.tcrmarks == null) this.tcrmarks = 100L;
        if (this.tcrstatus == null) this.tcrstatus = "AVAILABLE";
        if (this.tcrcreatedby == null) this.tcrcreatedby = 0L;
        if (this.tcrcreatedat == null) this.tcrcreatedat = LocalDateTime.now();
        if (this.tcrrowstate == null) this.tcrrowstate = 1L;
    }

    // Getters and Setters
    public Long getTcrid() { return tcrid; }
    public void setTcrid(Long tcrid) { this.tcrid = tcrid; }

    public Long getTcrtrmid() { return tcrtrmid; }
    public void setTcrtrmid(Long tcrtrmid) { this.tcrtrmid = tcrtrmid; }

    public Long getTcrcrsid() { return tcrcrsid; }
    public void setTcrcrsid(Long tcrcrsid) { this.tcrcrsid = tcrcrsid; }

    public String getTcrtype() { return tcrtype; }
    public void setTcrtype(String tcrtype) { this.tcrtype = tcrtype; }

    public Long getTcrfacultyid() { return tcrfacultyid; }
    public void setTcrfacultyid(Long tcrfacultyid) { this.tcrfacultyid = tcrfacultyid; }

    public String getTcrroundlogic() { return tcrroundlogic; }
    public void setTcrroundlogic(String tcrroundlogic) { this.tcrroundlogic = tcrroundlogic; }

    public Long getTcrmarks() { return tcrmarks; }
    public void setTcrmarks(Long tcrmarks) { this.tcrmarks = tcrmarks; }

    public String getTcrstatus() { return tcrstatus; }
    public void setTcrstatus(String tcrstatus) { this.tcrstatus = tcrstatus; }

    public String getTcraccessstatus() { return tcrAccessStatus; }
    public void setTcraccessstatus(String tcrAccessStatus) { this.tcrAccessStatus = tcrAccessStatus; }

    public Long getTcrcreatedby() { return tcrcreatedby; }
    public void setTcrcreatedby(Long tcrcreatedby) { this.tcrcreatedby = tcrcreatedby; }

    public LocalDateTime getTcrcreatedat() { return tcrcreatedat; }
    public void setTcrcreatedat(LocalDateTime tcrcreatedat) { this.tcrcreatedat = tcrcreatedat; }

    public Long getTcrlastupdatedby() { return tcrlastupdatedby; }
    public void setTcrlastupdatedby(Long tcrlastupdatedby) { this.tcrlastupdatedby = tcrlastupdatedby; }

    public LocalDateTime getTcrlastupdatedat() { return tcrlastupdatedat; }
    public void setTcrlastupdatedat(LocalDateTime tcrlastupdatedat) { this.tcrlastupdatedat = tcrlastupdatedat; }

    public Long getTcrrowstate() { return tcrrowstate; }
    public void setTcrrowstate(Long tcrrowstate) { this.tcrrowstate = tcrrowstate; }

//    public String getSlot() { return slot; }
//    public void setSlot(String slot) { this.slot = slot; }

    public Long getTcrslot() { return tcrslot; }
    public void setTcrslot(Long tcrslot) { this.tcrslot = tcrslot; }

    public String getCrstype() { return crstype; }
    public void setCrstype(String crstype) { this.crstype = crstype; }

    public Courses getCourse() {
        return course;
    }
    public void setCourse(Courses course) {
        this.course = course;
    }

    public Users getUser() {
        return user;
    }
    public void setUser(Users user) {
        this.user = user;
    }

    public Terms getTerms() {
        return term;
    }
    public void setTerms(Terms terms) {
        this.term = terms;
    }
}

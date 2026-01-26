package com.ecampus.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "semesters", schema="ec2")
public class Semesters {

    @Id
    @Column(name = "strid")
    private Long strid;

    @Column(name = "strcalid")
    private Long strcalid;

    @Column(name = "strname")
    private String strname = "U";

    @Column(name = "strfield1")
    private String strfield1 = "Y";

    @Column(name = "strfield2")
    private String strfield2 = "T";

    @Column(name = "strfield3")
    private String strfield3 =  "T";

    @Column(name = "strcreatedby")
    private Long strcreatedby = 0L;

    @Column(name = "strcreatedat")
    private LocalDateTime strcreatedat = LocalDateTime.now();

    @Column(name = "strlastupdatedby")
    private Long strlastupdatedby = 0L;

    @Column(name = "strlastupdatedat")
    private LocalDateTime strlastupdatedat = LocalDateTime.now();

    @Column(name = "strrowstate")
    private Long strrowstate = 1L;

    @Column(name = "strseqno")
    private Long strseqno = 0L;

    @Column(name = "strstcid")
    private Long strstcid = 1L;

    @Column(name = "strresultdecdate")
    private LocalDateTime strresultdecdate = LocalDateTime.now();

    @Column(name = "strregstatus")
    private String strregstatus;

    @Column(name = "stradddropstatus")
    private String stradddropstatus;

    @Column(name = "strbchid", insertable = false, updatable = false)
    private Long strbchid = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strbchid", referencedColumnName = "bchid", nullable = false)
    @JsonIgnore
    private Batches batches;

    @Column(name = "strtrmid", insertable = false, updatable = false)
    private Long strtrmid = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strtrmid", referencedColumnName = "trmid", nullable = false)
    @JsonIgnore
    private Terms terms;

    @PrePersist
    public void prePersist() {
        if (this.strname == null) this.strname = "U";
        if (this.strfield1 == null) this.strfield1 = "Y";
        if (this.strfield2 == null) this.strfield2 = "T";
        if (this.strfield3 == null) this.strfield3 = "T";
        if (this.strcreatedat == null) this.strcreatedat = LocalDateTime.now();
        if (this.strlastupdatedat == null) this.strlastupdatedat = LocalDateTime.now();
        if (this.strresultdecdate == null) this.strresultdecdate = LocalDateTime.now();
        if (this.strcreatedby == null) this.strcreatedby = 0L;
        if (this.strlastupdatedby == null) this.strlastupdatedby = 0L;
        if (this.strbchid == null) this.strbchid = 0L;
        if (this.strtrmid == null) this.strtrmid = 0L;
        if (this.strrowstate == null) this.strrowstate = 1L;
        if (this.strseqno == null) this.strseqno = 0L;
        if (this.strstcid == null) this.strstcid = 1L;
    }

    public Batches getBatches() {
        return batches;
    }
    public void setBatches(Batches batches) {
        this.batches = batches;
    }
    public Terms getTerms() {
        return terms;
    }
    public void setTerms(Terms terms) {
        this.terms = terms;
    }
    public Long getStrid() { return strid; }
    public void setStrid(Long strid) { this.strid = strid; }

    public Long getStrbchid() { return strbchid; }
    public void setStrbchid(Long strbchid) { this.strbchid = strbchid; }

    public Long getStrtrmid() { return strtrmid; }
    public void setStrtrmid(Long strtrmid) { this.strtrmid = strtrmid; }

    public Long getStrcalid() { return strcalid; }
    public void setStrcalid(Long strcalid) { this.strcalid = strcalid; }

    public String getStrname() { return strname; }
    public void setStrname(String strname) { this.strname = strname; }

    public String getStrfield1() { return strfield1; }
    public void setStrfield1(String strfield1) { this.strfield1 = strfield1; }

    public String getStrfield2() { return strfield2; }
    public void setStrfield2(String strfield2) { this.strfield2 = strfield2; }

    public String getStrfield3() { return strfield3; }
    public void setStrfield3(String strfield3) { this.strfield3 = strfield3; }

    public Long getStrcreatedby() { return strcreatedby; }
    public void setStrcreatedby(Long strcreatedby) { this.strcreatedby = strcreatedby; }

    public LocalDateTime getStrcreatedat() { return strcreatedat; }
    public void setStrcreatedat(LocalDateTime strcreatedat) { this.strcreatedat = strcreatedat; }

    public Long getStrlastupdatedby() { return strlastupdatedby; }
    public void setStrlastupdatedby(Long strlastupdatedby) { this.strlastupdatedby = strlastupdatedby; }

    public LocalDateTime getStrlastupdatedat() { return strlastupdatedat; }
    public void setStrlastupdatedat(LocalDateTime strlastupdatedat) { this.strlastupdatedat = strlastupdatedat; }

    public Long getStrrowstate() { return strrowstate; }
    public void setStrrowstate(Long strrowstate) { this.strrowstate = strrowstate; }

    public Long getStrseqno() { return strseqno; }
    public void setStrseqno(Long strseqno) { this.strseqno = strseqno; }

    public Long getStrstcid() { return strstcid; }
    public void setStrstcid(Long strstcid) { this.strstcid = strstcid; }

    public LocalDateTime getStrresultdecdate() { return strresultdecdate; }
    public void setStrresultdecdate(LocalDateTime strresultdecdate) { this.strresultdecdate = strresultdecdate; }

    public String getStrregstatus() { return strregstatus; }
    public void setStrregstatus(String strregstatus) { this.strregstatus = strregstatus; }

    public String getStradddropstatus() { return stradddropstatus; }
    public void setStradddropstatus(String stradddropstatus) { this.stradddropstatus = stradddropstatus; }

}

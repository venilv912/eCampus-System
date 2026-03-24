package com.ecampus.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "batches", schema="ec2")
public class Batches {

    @Id
    @Column(name = "bchid")
    private Long bchid;

    @Column(name = "bchprgid", insertable = false, updatable = false)
    private Long bchprgid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bchprgid", nullable = false)
    @JsonIgnore
    private Programs programs;

    public Programs getPrograms() {
        return programs;
    }
    public void setPrograms(Programs programs) {
        this.programs = programs;
    }
    @Column(name = "bchcalid")
    private Long bchcalid;

    @Column(name = "bchname")
    private String bchname;

    @Column(name = "bchcapacity")
    private Long bchcapacity;

    @Column(name = "bchfield1")
    private Long bchfield1;

    @Column(name = "bchcreatedby")
    private Long bchcreatedby;

    @Column(name = "bchcreatedat")
    private LocalDateTime bchcreatedat;

    @Column(name = "bchlastupdatedby")
    private Long bchlastupdatedby;

    @Column(name = "bchlastupdatedat")
    private LocalDateTime bchlastupdatedat;

    @Column(name = "bchrowstate")
    private Long bchrowstate;

    @Column(name = "bchinstcode")
    private String bchinstcode;

    @Column(name = "scheme_id")
    private Long schemeId;

    @Column(name = "splid")
    private Long splid;

    @ManyToOne
    @JoinColumns({
           @JoinColumn(name = "scheme_id", referencedColumnName = "scheme_id", insertable = false, updatable = false),
           @JoinColumn(name = "splid", referencedColumnName = "splid", insertable = false, updatable = false)
    })
    private SchemeDetails schemeDetails;

    public Long getBchid() { return bchid; }
    public void setBchid(Long bchid) { this.bchid = bchid; }

    public Long getBchprgid() { return bchprgid; }
    public void setBchprgid(Long bchprgid) { this.bchprgid = bchprgid; }

    public Long getBchcalid() { return bchcalid; }
    public void setBchcalid(Long bchcalid) { this.bchcalid = bchcalid; }

    public String getBchname() { return bchname; }
    public void setBchname(String bchname) { this.bchname = bchname; }

    public Long getBchcapacity() { return bchcapacity; }
    public void setBchcapacity(Long bchcapacity) { this.bchcapacity = bchcapacity; }

    public Long getBchfield1() { return bchfield1; }
    public void setBchfield1(Long bchfield1) { this.bchfield1 = bchfield1; }

    public Long getBchcreatedby() { return bchcreatedby; }
    public void setBchcreatedby(Long bchcreatedby) { this.bchcreatedby = bchcreatedby; }

    public LocalDateTime getBchcreatedat() { return bchcreatedat; }
    public void setBchcreatedat(LocalDateTime bchcreatedat) { this.bchcreatedat = bchcreatedat; }

    public Long getBchlastupdatedby() { return bchlastupdatedby; }
    public void setBchlastupdatedby(Long bchlastupdatedby) { this.bchlastupdatedby = bchlastupdatedby; }

    public LocalDateTime getBchlastupdatedat() { return bchlastupdatedat; }
    public void setBchlastupdatedat(LocalDateTime bchlastupdatedat) { this.bchlastupdatedat = bchlastupdatedat; }

    public Long getBchrowstate() { return bchrowstate; }
    public void setBchrowstate(Long bchrowstate) { this.bchrowstate = bchrowstate; }

    public String getBchinstcode() { return bchinstcode; }
    public void setBchinstcode(String bchinstcode) { this.bchinstcode = bchinstcode; }

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public Long getSplid() { return splid; }
    public void setSplid(Long splid) { this.splid = splid; }

    public SchemeDetails getSchemeDetails() {return schemeDetails;}
    public void setSchemeDetails(SchemeDetails schemeDetails) {this.schemeDetails = schemeDetails;}
}

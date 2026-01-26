package com.ecampus.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "students", schema="ec2")
public class Students {

    @Id
    @Column(name = "stdid")
    private Long stdid;

    @Column(name = "stdinstid")
    private String stdinstid;

    @Column(name = "stdfirstname")
    private String stdfirstname;

    @Column(name = "stdmiddlename")
    private String stdmiddlename;

    @Column(name = "stdlastname")
    private String stdlastname;

    @Column(name = "stdid_2")
    private Long stdid2;

    @Column(name = "stdstatus")
    private String stdstatus;

    @Column(name = "stdcreatedby")
    private Long stdcreatedby;

    @Column(name = "stdcreatedat")
    private LocalDateTime stdcreatedat;

    @Column(name = "stdlastupdatedby")
    private Long stdlastupdatedby;

    @Column(name = "stdlastupdatedat")
    private LocalDateTime stdlastupdatedat;

    @Column(name = "stdrowstate")
    private Long stdrowstate;

    @Column(name = "stdfield1")
    private String stdfield1;

    @Column(name = "stdfield2")
    private String stdfield2;

    @Column(name = "stdfield3")
    private String stdfield3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stdbchid", referencedColumnName = "bchid")
    private Batches batch;

    @Column(name = "stdbchid", insertable = false, updatable = false)
    private Long stdbchid;

    @Column(name = "CURRADRID")
    private Long currAdrId;

    @Column(name = "PRMTADRID")
    private Long prmtAdrId;

    @Column(name = "EMGRADRID")
    private Long emrgAdrId;

    private String stdemail;


    public String getStdemail() {
        return stdemail;
    }

    public void setStdemail(String stdemail) {
        this.stdemail = stdemail;
    }

    public Batches getBatch() {
        return batch;
    }

    public void setBatch(Batches batch) {
        this.batch = batch;
    }

    public Long getCurrAdrId() {
        return currAdrId;
    }

    public void setCurrAdrId(Long currAdrId) {
        this.currAdrId = currAdrId;
    }

    public Long getPrmtAdrId() {
        return prmtAdrId;
    }

    public void setPrmtAdrId(Long prmtAdrId) {
        this.prmtAdrId = prmtAdrId;
    }

    public Long getEmrgAdrId() {
        return emrgAdrId;
    }

    public void setEmrgAdrId(Long emrgAdrId) {
        this.emrgAdrId = emrgAdrId;
    }

    public Long getStdid() { return stdid; }
    public void setStdid(Long stdid) { this.stdid = stdid; }

    public Long getStdbchid() { return stdbchid; }
    public void setStdbchid(Long stdbchid) { this.stdbchid = stdbchid; }

    public String getStdinstid() { return stdinstid; }
    public void setStdinstid(String stdinstid) { this.stdinstid = stdinstid; }

    public String getStdfirstname() { return stdfirstname; }
    public void setStdfirstname(String stdfirstname) { this.stdfirstname = stdfirstname; }

    public String getStdmiddlename() { return stdmiddlename; }
    public void setStdmiddlename(String stdmiddlename) { this.stdmiddlename = stdmiddlename; }

    public String getStdlastname() { return stdlastname; }
    public void setStdlastname(String stdlastname) { this.stdlastname = stdlastname; }

    public Long getStdid2() { return stdid2; }
    public void setStdid2(Long stdid2) { this.stdid2 = stdid2; }

    public String getStdstatus() { return stdstatus; }
    public void setStdstatus(String stdstatus) { this.stdstatus = stdstatus; }

    public Long getStdcreatedby() { return stdcreatedby; }
    public void setStdcreatedby(Long stdcreatedby) { this.stdcreatedby = stdcreatedby; }

    public LocalDateTime getStdcreatedat() { return stdcreatedat; }
    public void setStdcreatedat(LocalDateTime stdcreatedat) { this.stdcreatedat = stdcreatedat; }

    public Long getStdlastupdatedby() { return stdlastupdatedby; }
    public void setStdlastupdatedby(Long stdlastupdatedby) { this.stdlastupdatedby = stdlastupdatedby; }

    public LocalDateTime getStdlastupdatedat() { return stdlastupdatedat; }
    public void setStdlastupdatedat(LocalDateTime stdlastupdatedat) { this.stdlastupdatedat = stdlastupdatedat; }

    public Long getStdrowstate() { return stdrowstate; }
    public void setStdrowstate(Long stdrowstate) { this.stdrowstate = stdrowstate; }

    public String getStdfield1() { return stdfield1; }
    public void setStdfield1(String stdfield1) { this.stdfield1 = stdfield1; }

    public String getStdfield2() { return stdfield2; }
    public void setStdfield2(String stdfield2) { this.stdfield2 = stdfield2; }

    public String getStdfield3() { return stdfield3; }
    public void setStdfield3(String stdfield3) { this.stdfield3 = stdfield3; }

}

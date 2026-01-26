package com.ecampus.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "academicyears", schema="ec2")
public class AcademicYears {

    @Id
    @Column(name = "ayrid")
    private Long ayrid;

    @Column(name = "ayrname")
    private String ayrname;

    @Column(name = "ayrcreatedby")
    private Long ayrcreatedby;

    @Column(name = "ayrcreatedat")
    private LocalDateTime ayrcreatedat;

    @Column(name = "ayrlastupdatedby")
    private Long ayrlastupdatedby;

    @Column(name = "ayrlastupdatedat")
    private LocalDateTime ayrlastupdatedat;

    @Column(name = "ayrrowstate")
    private Long ayrrowstate;

    @Column(name = "ayrfield1")
    private Long ayrfield1;


    public Long getAyrid() { return ayrid; }
    public void setAyrid(Long ayrid) { this.ayrid = ayrid; }

    public String getAyrname() { return ayrname; }
    public void setAyrname(String ayrname) { this.ayrname = ayrname; }

    public Long getAyrcreatedby() { return ayrcreatedby; }
    public void setAyrcreatedby(Long ayrcreatedby) { this.ayrcreatedby = ayrcreatedby; }

    public LocalDateTime getAyrcreatedat() { return ayrcreatedat; }
    public void setAyrcreatedat(LocalDateTime ayrcreatedat) { this.ayrcreatedat = ayrcreatedat; }

    public Long getAyrlastupdatedby() { return ayrlastupdatedby; }
    public void setAyrlastupdatedby(Long ayrlastupdatedby) { this.ayrlastupdatedby = ayrlastupdatedby; }

    public LocalDateTime getAyrlastupdatedat() { return ayrlastupdatedat; }
    public void setAyrlastupdatedat(LocalDateTime ayrlastupdatedat) { this.ayrlastupdatedat = ayrlastupdatedat; }

    public Long getAyrrowstate() { return ayrrowstate; }
    public void setAyrrowstate(Long ayrrowstate) { this.ayrrowstate = ayrrowstate; }

    public Long getAyrfield1() { return ayrfield1; }
    public void setAyrfield1(Long ayrfield1) { this.ayrfield1 = ayrfield1; }

}

package com.ecampus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "adddroppref", schema="ec2")
public class AddDropPreferences {

    @Id
    @Column(name = "adpid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adpid;

    @Column(name = "sid")
    private Long sid;

    @Column(name = "addcount")
    private Long addcount;

    @Column(name = "addp1")
    private String addp1;

    @Column(name = "addp2")
    private String addp2;

    @Column(name = "addp3")
    private String addp3;

    @Column(name = "addp4")
    private String addp4;

    @Column(name = "drop1")
    private String drop1;

    @Column(name = "drop1_p1")
    private String drop1_p1;

    @Column(name = "drop1_p2")
    private String drop1_p2;

    @Column(name = "drop1_p3")
    private String drop1_p3;

    @Column(name = "drop2")
    private String drop2;

    @Column(name = "drop2_p1")
    private String drop2_p1;

    @Column(name = "drop2_p2")
    private String drop2_p2;

    @Column(name = "drop2_p3")
    private String drop2_p3;

    @Column(name = "drop3")
    private String drop3;

    @Column(name = "drop3_p1")
    private String drop3_p1;

    @Column(name = "drop3_p2")
    private String drop3_p2;

    @Column(name = "drop3_p3")
    private String drop3_p3;

    // Relationship
    @ManyToOne
    @JoinColumn(name = "sid", referencedColumnName = "stdid", insertable = false, updatable = false)
    private Students student;

    // Getters And Setters

    public Long getAdpid(){ return adpid; }
    public void setAdpid(Long adpid) { this.adpid = adpid; }

    public Long getSid(){ return sid; }
    public void setSid(Long sid) { this.sid = sid; }

    public Long getAddcount(){ return addcount; }
    public void setAddcount(Long addcount) { this.addcount = addcount; }

    public String getAddp1(){ return addp1; }
    public void setAddp1(String addp1) { this.addp1 = addp1; }

    public String getAddp2(){ return addp2; }
    public void setAddp2(String addp2) { this.addp2 = addp2; }

    public String getAddp3(){ return addp3; }
    public void setAddp3(String addp3) { this.addp3 = addp3; }

    public String getAddp4(){ return addp4; }
    public void setAddp4(String addp4) { this.addp4 = addp4; }

    public String getDrop1(){ return drop1; }
    public void setDrop1(String drop1){ this.drop1 = drop1; }

    public String getDrop1_p1(){ return drop1_p1; }
    public void setDrop1_p1(String drop1_p1){ this.drop1_p1 = drop1_p1; }

    public String getDrop1_p2(){ return drop1_p2; }
    public void setDrop1_p2(String drop1_p2){ this.drop1_p2 = drop1_p2; }

    public String getDrop1_p3(){ return drop1_p3; }
    public void setDrop1_p3(String drop1_p3){ this.drop1_p3 = drop1_p3; }

    public String getDrop2(){ return drop2; }
    public void setDrop2(String drop2){ this.drop2 = drop2; }

    public String getDrop2_p1(){ return drop2_p1; }
    public void setDrop2_p1(String drop2_p1){ this.drop2_p1 = drop2_p1; }

    public String getDrop2_p2(){ return drop2_p2; }
    public void setDrop2_p2(String drop2_p2){ this.drop2_p2 = drop2_p2; }

    public String getDrop2_p3(){ return drop2_p3; }
    public void setDrop2_p3(String drop2_p3){ this.drop2_p3 = drop2_p3; }

    public String getDrop3(){ return drop3; }
    public void setDrop3(String drop3){ this.drop3 = drop3; }

    public String getDrop3_p1(){ return drop3_p1; }
    public void setDrop3_p1(String drop3_p1){ this.drop3_p1 = drop3_p1; }

    public String getDrop3_p2(){ return drop3_p2; }
    public void setDrop3_p2(String drop3_p2){ this.drop3_p2 = drop3_p2; }

    public String getDrop3_p3(){ return drop3_p3; }
    public void setDrop3_p3(String drop3_p3){ this.drop3_p3 = drop3_p3; }

    public Students getStudent(){ return student; }
    public void setStudent(Students student){ this.student = student; }
    
}

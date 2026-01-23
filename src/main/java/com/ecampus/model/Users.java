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
@Table(name = "users", schema="ec2")
public class Users {

    @Id
    @Column(name = "uid")
    private Long uid;

    @Column(name = "univid")
    private String univId;

    @Column(name = "stdid")
    private Long stdid;

    @Column(name = "uname")
    private String uname;

    @Column(name = "ufullname")
    private String ufullname;

    @Column(name = "utype")
    private String utype;

    @Column(name = "utype_0")
    private String utype0;

    @Column(name = "urole")
    private String urole;

    @Column(name = "urole_0")
    private String urole0;

    @Column(name = "uemail")
    private String uemail;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "create_ts")
    private LocalDateTime createTs;

    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;

    @Column(name = "last_updated_ts")
    private LocalDateTime lastUpdatedTs;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "row_state")
    private Short rowState;

    @Column(name = "uid_older")
    private Long uidOlder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "urole", referencedColumnName = "rid", insertable = false, updatable = false)
    private Role role;

    public Users() {

    }

    public Users(String userName, String userMailId, String userCategory, Long userId) {
        this.uname = userName;
        this.uemail = userMailId;
        this.urole0 = userCategory;
        this.uid = userId;
    }
    // Getters and Setters
    public Long getUid() { return uid; }
    public void setUid(Long uid) { this.uid = uid; }

    public String getUnivId() { return univId; }
    public void setUnivId(String univId) { this.univId = univId; }

    public Long getStdid() { return stdid; }
    public void setStdid(Long stdid) { this.stdid = stdid; }

    public String getUname() { return uname; }
    public void setUname(String uname) { this.uname = uname; }

    public String getUfullname() { return ufullname; }
    public void setUfullname(String ufullname) { this.ufullname = ufullname; }

    public String getUtype() { return utype; }
    public void setUtype(String utype) { this.utype = utype; }

    public String getUtype0() { return utype0; }
    public void setUtype0(String utype0) { this.utype0 = utype0; }

    public String getUrole() { return urole; }
    public void setUrole(String urole) { this.urole = urole; }

    public String getUrole0() { return urole0; }
    public void setUrole0(String urole0) { this.urole0 = urole0; }

    public String getUemail() { return uemail; }
    public void setUemail(String uemail) { this.uemail = uemail; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreateTs() { return createTs; }
    public void setCreateTs(LocalDateTime createTs) { this.createTs = createTs; }

    public Long getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(Long lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }

    public LocalDateTime getLastUpdatedTs() { return lastUpdatedTs; }
    public void setLastUpdatedTs(LocalDateTime lastUpdatedTs) { this.lastUpdatedTs = lastUpdatedTs; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public Short getRowState() { return rowState; }
    public void setRowState(Short rowState) { this.rowState = rowState; }

    public Long getUidOlder() { return uidOlder; }
    public void setUidOlder(Long uidOlder) { this.uidOlder = uidOlder; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

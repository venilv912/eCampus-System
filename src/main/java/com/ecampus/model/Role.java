package com.ecampus.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ec2_roles", schema="ec2")
public class Role {

    @Id
    @Column(name = "rid")
    private String rid;

    @Column(name = "rolename")
    private String roleName;

    @Column(name = "comments")
    private String comments;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "create_ts")
    private LocalDateTime createTs;

    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;

    @Column(name = "last_updated_ts")
    private LocalDateTime lastUpdatedTs;

    @Column(name = "row_state")
    private Short rowState;

    // Getters and Setters
    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreateTs() {
        return createTs;
    }

    public void setCreateTs(LocalDateTime createTs) {
        this.createTs = createTs;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public LocalDateTime getLastUpdatedTs() {
        return lastUpdatedTs;
    }

    public void setLastUpdatedTs(LocalDateTime lastUpdatedTs) {
        this.lastUpdatedTs = lastUpdatedTs;
    }

    public Short getRowState() {
        return rowState;
    }

    public void setRowState(Short rowState) {
        this.rowState = rowState;
    }
}

package com.internship.Project.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EmailDetails {

    // The Serial number for the table
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long srNo;
    // The Email ID's
    @javax.validation.constraints.Email
    private String emailId;
    // The matching instance name for the email id
    private String instanceName;

    public Long getSrNo() {
        return srNo;
    }

    public void setSrNo(Long srNo) {
        this.srNo = srNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
}
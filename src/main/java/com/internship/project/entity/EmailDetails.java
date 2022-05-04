package com.internship.project.entity;

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
    // The jobClassName for which then email needs to be sent
    private String jobClassName;

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

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }
}
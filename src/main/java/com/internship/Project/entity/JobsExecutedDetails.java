package com.internship.Project.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class JobsExecutedDetails {

    // The table ID
    @Id
    @GeneratedValue
    private Long jobsExecutedId;
    // The name of the Instance in which the job has been executed
    private String instanceName;
    // The execution time of the job
    @CreationTimestamp
    private LocalDateTime executionTime;
    // The status of the job
    private String executionStatus;
    // The message based on the status of the job
    private String executionStatusMessage;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", referencedColumnName = "job_id")
    private Jobs jobs;

    public Long getJobsExecutedId() {
        return jobsExecutedId;
    }

    public void setJobsExecutedId(Long jobsExecutedId) {
        this.jobsExecutedId = jobsExecutedId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String getExecutionStatusMessage() {
        return executionStatusMessage;
    }

    public void setExecutionStatusMessage(String executionStatusMessage) {
        this.executionStatusMessage = executionStatusMessage;
    }

    public Jobs getJobs() {
        return jobs;
    }

    public void setJobs(Jobs jobs) {
        this.jobs = jobs;
    }
}

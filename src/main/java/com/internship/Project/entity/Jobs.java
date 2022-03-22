package com.internship.Project.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "jobs")
public class Jobs {

    // ID for each entry in the table
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "job_id")
    private Long jobId;
    // Name of each job
    @Column(unique = true)
    private String name;
    // Job store type to which the job is memory/jdbc
    private String memoryType;
    // Cron expression
    private String cronExpression;
    // The class which has to implement the job
    private String jobClass;
    // Tells at what time its created
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    // Tells at what time its updated
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // This is the field which manages the status of the Job(unscheduled, active, suspended, inactive)
    private String jobWorkingStatus;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getJobWorkingStatus() {
        return jobWorkingStatus;
    }

    public void setJobWorkingStatus(String jobWorkingStatus) {
        this.jobWorkingStatus = jobWorkingStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package com.internship.Project.scheduler;

import com.internship.Project.repository.CurrencyDataRepo;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;

import java.io.Serializable;

public class Configurations implements Serializable {

    JobsRepo jobsRepo;

    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    CurrencyDataRepo currencyDataRepo;

    public Configurations(JobsRepo jobsRepo, JobExecutedDetailsRepo jobExecutedDetailsRepo, CurrencyDataRepo currencyDataRepo) {
        this.jobsRepo = jobsRepo;
        this.jobExecutedDetailsRepo = jobExecutedDetailsRepo;
        this.currencyDataRepo = currencyDataRepo;
    }

    public JobsRepo getJobsRepo() {
        return jobsRepo;
    }

    public JobExecutedDetailsRepo getJobExecutedDetailsRepo() {
        return jobExecutedDetailsRepo;
    }

    public CurrencyDataRepo getCurrencyDataRepo() {
        return currencyDataRepo;
    }

}

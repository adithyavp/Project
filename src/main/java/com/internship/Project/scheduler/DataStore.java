package com.internship.Project.scheduler;

import com.internship.Project.repository.CurrencyDataRepo;
import com.internship.Project.repository.EmailDetailsRepo;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class DataStore {


    static String string;
    static Process process;
    static String instanceName = "Docker ID";

    public static String getInstanceName() {

        try {
            process = Runtime.getRuntime().exec("uname -n");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((string = bufferedReader.readLine())!=null){
                System.out.println(string);
                instanceName = string;
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            process.exitValue();
            process.destroy();

            log.info("Docker instance name read successful");
        } catch (IOException e) {
            log.error("Error while retrieving Docker instance name", e);
        }

        return instanceName;
    }

    public static void setInstanceName(String instanceName) {
        DataStore.instanceName = instanceName;
    }

    @Autowired
    static JobsRepo jobsRepo;

    @Autowired
    static CurrencyDataRepo currencyDataRepo;

    @Autowired
    static EmailDetailsRepo emailDetailsRepo;

    @Autowired
    static JobExecutedDetailsRepo jobExecutedDetailsRepo;

    public static JobsRepo getJobsRepo() {
        return jobsRepo;
    }

    public static void setJobsRepo(JobsRepo jobsRepo) {
        DataStore.jobsRepo = jobsRepo;
    }

    public static CurrencyDataRepo getCurrencyDataRepo() {
        return currencyDataRepo;
    }

    public static void setCurrencyDataRepo(CurrencyDataRepo currencyDataRepo) {
        DataStore.currencyDataRepo = currencyDataRepo;
    }

    public static EmailDetailsRepo getEmailDetailsRepo() {
        return emailDetailsRepo;
    }

    public static void setEmailDetailsRepo(EmailDetailsRepo emailDetailsRepo) {
        DataStore.emailDetailsRepo = emailDetailsRepo;
    }

    public static JobExecutedDetailsRepo getJobExecutedDetailsRepo() {
        return jobExecutedDetailsRepo;
    }

    public static void setJobExecutedDetailsRepo(JobExecutedDetailsRepo jobExecutedDetailsRepo) {
        DataStore.jobExecutedDetailsRepo = jobExecutedDetailsRepo;
    }
}

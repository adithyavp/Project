package com.internship.Project.service;


import com.internship.Project.entity.EmailDetails;
import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.repository.EmailDetailsRepo;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GlobalRestService {

    @Autowired
    JobsRepo jobsRepo;

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    EmailDetailsRepo emailDetailsRepo;

    @Autowired
    MainService mainService;

    // For the Jobs master table
    //
    //
    //

    // Creation of the job details in the jobs table
    public Jobs createJob(Jobs job){
        Jobs newJobToCreate = jobsRepo.findByName(job.getName());
        if(newJobToCreate!=null){
            System.out.println("Job is already created");
        }
        else{
            job.setJobWorkingStatus("unscheduled");
            newJobToCreate = jobsRepo.save(job);
        }

        return  newJobToCreate;
    }

    // To read all the jobs from the jobs table
    public List<Jobs> readAllJobs(){
        Iterable<Jobs> allJobs = jobsRepo.findAll();
        return (List<Jobs>) allJobs;
    }

    // To update the jobs in the Jobs table
    // One can update only the Memory type and the Cron expression and no other data
    public Jobs updateJob(Long jobId, String jobName, Jobs job){
        Jobs isJobPresent = jobsRepo.findByJobIdAndName(jobId, jobName);
        if(isJobPresent!=null){
            isJobPresent.setMemoryType(job.getMemoryType());
            isJobPresent.setExecutionClass(job.getExecutionClass());
            isJobPresent.setCronExpression(job.getCronExpression());

            jobsRepo.save(isJobPresent);
        } else{
            System.out.println("Job Not Found");
        }
        return isJobPresent;
    }

    // To delete the jobs from the jobs table
    public void deleteJob(Long jobId){
        Optional<Jobs> jobToDelete = jobsRepo.findById(jobId);
        if(jobToDelete.isPresent()){
            jobsRepo.delete(jobToDelete.get());
        } else{
            System.out.println("Job not present");
        }
    }

    // Scheduling all Global jobs created
    public void startGlobalJobScheduling(){
        mainService.scheduleAllGlobalJob();
    }

    // Scheduling all Local jobs created
    public void startLocalJobScheduling(){
        mainService.scheduleAllLocalJob();
    }



    // For the Jobs Executed Details table
    //
    //
    //

    // To read all the jobs from the jobs table
    public List<JobsExecutedDetails> readAllJobsExecuted(){
        Iterable<JobsExecutedDetails> allExecutedJobs = jobExecutedDetailsRepo.findAll();
        return (List<JobsExecutedDetails>) allExecutedJobs;
    }


    // For the Email Details table
    //
    //
    //
    String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    Pattern pattern = Pattern.compile(regex);

    // To create new Email Detail
    public EmailDetails createEmailDetail(EmailDetails emailDetail) {

        Matcher matcher = pattern.matcher(emailDetail.getEmailId());
        if(!matcher.matches()){
            System.out.println("Email Id is incorrect please check Email Id");
        } else{
            EmailDetails newEmailDetailsToCreate = emailDetailsRepo.findByEmailIdAndJobClassName(emailDetail.getEmailId(), emailDetail.getJobClassName());
            if(newEmailDetailsToCreate!=null){
                emailDetail = newEmailDetailsToCreate;
                System.out.println("Email Detail is already created");
            }
            else{
                emailDetail = emailDetailsRepo.save(emailDetail);
            }
        }
        return emailDetail;
    }

    // To read all Email Details
    public List<EmailDetails> readAllEmailDetails() {

        Iterable<EmailDetails> findAllEmailDetails = emailDetailsRepo.findAll();
        return (List<EmailDetails>) findAllEmailDetails;

    }

    // To update Email Detail
    public EmailDetails updateEmailDetail(Long srNo, String emailId, EmailDetails updateEmailDetail) {

        EmailDetails isEmailDetailPresent = emailDetailsRepo.findBySrNoAndEmailId(srNo, emailId);
        if(isEmailDetailPresent!=null) {

            Matcher matcher = pattern.matcher(updateEmailDetail.getEmailId());
            if (!matcher.matches()) {
                System.out.println("Email Id is incorrect please check Email Id");
            } else {
                isEmailDetailPresent.setEmailId(updateEmailDetail.getEmailId());
                isEmailDetailPresent.setJobClassName(updateEmailDetail.getJobClassName());

                emailDetailsRepo.save(isEmailDetailPresent);
            }

        } else{
            System.out.println("Email Detail not found");
        }
        return isEmailDetailPresent;
    }

    // To delete Email Detail
    public void deleteEmailDetail(String emailId, String jobClassName) {
        EmailDetails emailDetailToDelete = emailDetailsRepo.findByEmailIdAndJobClassName(emailId, jobClassName);
        if(emailDetailToDelete!=null){
            emailDetailsRepo.delete(emailDetailToDelete);
        } else{
            System.out.println("Email Detail not present");
        }
    }

    // Just for check
    // To print hashMap of Trigger Key
    public void printHashMap() {
        mainService.printHashMap();
    }
}
package com.internship.Project;

import com.internship.Project.entity.Jobs;
import com.internship.Project.repository.JobsRepo;
import com.internship.Project.service.GlobalRestService;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Iterator;

@SpringBootApplication
public class ProjectApplication {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectApplication.class);

	@Autowired
	Scheduler scheduler;

	@Autowired
	JobsRepo jobsRepo;
	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@PostConstruct
	public void postConstruct(){
		try {
			scheduler.start();
			LOG.info("Quartz Scheduler started");
		} catch (SchedulerException e) {
			LOG.error("Quartz scheduler exception while starting: ", e);
		}
	}

	@PreDestroy
	public void preDestroy(){
		try {
			Iterable<Jobs> iterable = jobsRepo.findByJobWorkingStatus("active");
			for (Jobs job : iterable) {
				job.setJobWorkingStatus("unscheduled");

				jobsRepo.save(job);

				JobKey jobKey = new JobKey(job.getName(), job.getMemoryType());

				scheduler.deleteJob(jobKey);
			}

			scheduler.shutdown();

			LOG.info("Quartz Scheduler shutdown");
			LOG.info("The active jobs status updated to unscheduled, the jobs related to it have been deleted - Jobs Master Table, Quartz Job Tables(qrtz_triggers, qrtz_cron_triggers, qrtz_job_details)");
		} catch (SchedulerException e) {
			LOG.error("Quartz scheduler exception while shutting down: ");
		}
	}

}

package com.internship.Project;

import com.internship.Project.service.GlobalRestService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@SpringBootApplication
public class ProjectApplication {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectApplication.class);

	@Autowired
	Scheduler scheduler;

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@PostConstruct
	public void postConstruct(){
		try {
			scheduler.start();
			LOG.info("Scheduler has started");
		} catch (SchedulerException e) {
			LOG.error("Quartz scheduler exception while starting: ", e);
		}
	}

	@PreDestroy
	public void preDestroy(){
		try {
			scheduler.shutdown();
			LOG.info("Scheduler has shutdown");
		} catch (SchedulerException e) {
			LOG.error("Quartz scheduler exception while shutting down: ");
		}
	}


}

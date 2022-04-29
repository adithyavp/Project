package com.internship.Project.configuration;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
@ComponentScan("com.internship.Project.*")
public class SpringQuartzFactoryConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Bean(value = "getGlobalJobScheduler")
    public Scheduler schedulerGlobal() throws SchedulerException {

        StdSchedulerFactory stdSchedulerFactoryGlobal = new StdSchedulerFactory("globalQuartz.properties");

        Scheduler globalScheduler = stdSchedulerFactoryGlobal.getScheduler();

        globalScheduler.setJobFactory(springBeanJobFactory());

        return globalScheduler;
    }

    @Bean(value = "getLocalJobScheduler")
    public Scheduler schedulerLocal() throws SchedulerException {

        StdSchedulerFactory stdSchedulerFactoryLocal = new StdSchedulerFactory("localQuartz.properties");

        Scheduler localScheduler = stdSchedulerFactoryLocal.getScheduler();

        localScheduler.setJobFactory(springBeanJobFactory());

        return localScheduler;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

}

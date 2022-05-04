package com.internship.project.component;

import com.internship.project.service.GlobalSchedulerService;
import com.internship.project.service.LocalSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OnDestroy {

    @Autowired
    private GlobalSchedulerService globalSchedulerService;

    @Autowired
    private LocalSchedulerService localSchedulerService;

    @Value("${spring.quartz.job-store-type}")
    String jobStore;

    /* Run this when the application is stopping */
    @EventListener(ContextClosedEvent.class)
    public void runOnDestroy(){
        log.info("-------OnDestroy class called-------");
        if (jobStore.equals("jdbc")){
            globalSchedulerService.stop();
        }
        if (jobStore.equals("memory")){
            localSchedulerService.stop();
        }
    }

}

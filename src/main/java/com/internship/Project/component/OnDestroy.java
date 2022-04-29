package com.internship.Project.component;

import com.internship.Project.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class OnDestroy {

    @Autowired
    private MainService mainService;

    /* Run this when the application is stopping */
    @EventListener(ContextClosedEvent.class)
    public void runOnDestroy(){
        log.info("-------OnDestroy class called-------");
        mainService.stop();
    }

}

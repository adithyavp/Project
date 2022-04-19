package com.internship.Project.Component;

import com.internship.Project.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class OnDestroy {

    @Autowired
    private MainService mainService;

    /* Run this when the application is stopping */
    @PreDestroy
    public void runOnDestroy(){
        log.info("-------OnDestroy class called-------");
        mainService.stop();
    }

}

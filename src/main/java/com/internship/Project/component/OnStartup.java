package com.internship.Project.component;

import com.internship.Project.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OnStartup {

	@Autowired
	private MainService mainService;

	/* Run this when the application is starting */
	@EventListener(ApplicationReadyEvent.class)
	public void runOnStartUp() throws Exception {
		log.info("-------OnStartup class called-------");
		mainService.start();
	}
}
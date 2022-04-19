package com.internship.Project.Component;

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

	/* Run this when the application starts */
	@EventListener(ApplicationReadyEvent.class)
	public void runOnStartUp() throws Exception {
		log.info("-------OnStartup class called-------");
		mainService.start();
	}
}
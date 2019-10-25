package com.siteparser.MyOwnSiteParser.listener;

import com.siteparser.MyOwnSiteParser.service.jpa.UserService;
import com.siteparser.MyOwnSiteParser.service.parser.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppReadyListener {

    @Value("${launchMode}")
    private String launchMode;

    @Autowired
    private ParserService parserService;

    @Autowired
    private UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void appReady() {

        if (userService.getByEmail("admin") == null) userService.createAdmin();

        if (launchMode != null && launchMode.equals("cli")){

        }
    }

}

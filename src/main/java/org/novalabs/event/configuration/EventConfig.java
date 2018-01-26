package org.novalabs.event.configuration;

import org.novalabs.event.controller.EventController;
import org.novalabs.event.dao.EventDao;
import org.novalabs.event.service.EventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring Boot configuration file for the Event Service.
 */
@Configuration
public class EventConfig {

    @Bean("eventService")
    public EventService eventService(EventDao eventDao) {
        return new EventService(eventDao);
    }

    @Bean("eventController")
    public EventController eventController(EventService eventService) {
        return new EventController(eventService);
    }

}

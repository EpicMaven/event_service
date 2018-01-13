
package org.novalabs.event.controller;

import java.time.Instant;
import java.util.List;
import org.novalabs.event.domain.Event;
import org.novalabs.event.domain.EventCount;
import org.novalabs.event.service.EventService;
import org.novalabs.event.util.TimingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST controller for the Event Service.  The controller is responsible for handling the RESTful requests and responses.
 */
@RestController
@RequestMapping({"/"})
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    /**
     * Creates an instance of the controller, delegating business logic to the EventService.
     * @param eventService  the core business logic class
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Returns an array of the latest event for each event type.
     * @return  the array of latest events
     */
    @GetMapping({"/events"})
    @ResponseBody
    public List<Event> latestEvents() {
        long start = System.currentTimeMillis();
        logger.info("latestEvents");
        List<Event> events = this.eventService.latestEvents();
        logger.info("latestEvents | size {} | duration {}", events.size(), TimingUtils.duration(start));
        return events;
    }

    /**
     * Returns all events for an event type.
     * @param type  the event type
     * @return  the array of events, ordered in reverse chronology (latest first)
     */
    @GetMapping({"/events/{type}"})
    @ResponseBody
    public List<Event> allEvents(@PathVariable("type") String type) {
        long start = System.currentTimeMillis();
        logger.info("allEvents");
        List<Event> events = this.findByDateRange(type, 0L, Instant.now().toEpochMilli());
        logger.info("allEvents | size {} | duration {}", events.size(), TimingUtils.duration(start));
        return events;
    }

    /**
     * Returns the latest event for the event type.
     * @param type  the event type
     * @return  the latest event
     */
    @GetMapping({"/events/{type}/latest"})
    @ResponseBody
    public ResponseEntity<Event> latestEvent(@PathVariable("type") String type) {
        long start = System.currentTimeMillis();
        logger.info("latestEvent | type {}", type);
        Event event = this.eventService.latestEvent(type);
        logger.info("latestEvent finished | event {} | duration {}", event, TimingUtils.duration(start));
        ResponseEntity<Event> response;
        if (event != null) {
            response = new ResponseEntity<>(event, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * Returns all events for an event type.
     * @param type  the event type
     * @return  the array of events, ordered in reverse chronology (latest first)
     */
    @GetMapping({"/events/{type}/all"})
    @ResponseBody
    public List<Event> allEventsForType(@PathVariable("type") String type) {
        long start = System.currentTimeMillis();
        logger.info("allEventsForType");
        List<Event> events = this.findByDateRange(type, 0L, Instant.now().toEpochMilli());
        logger.info("allEventsForType | size {} | duration {}", events.size(), TimingUtils.duration(start));
        return events;
    }

    /**
     * Returns the count of event for an event type.
     * @param type  the event type
     * @return  the count of events
     */
    @GetMapping({"/events/{type}/count"})
    @ResponseBody
    public EventCount countEvents(@PathVariable("type") String type) {
        long start = System.currentTimeMillis();
        logger.info("countEvents");
        EventCount eventCount = this.eventService.countEvents(type);
        logger.info("countEvents | type {} | count {} | duration {}", type, eventCount.getCount(), TimingUtils.duration(start));
        return eventCount;
    }

    /**
     * Returns the list of events from the earliest time to now.  Times are milliseconds since the epoch.  The list is
     * returned in reverse chronological order.
     * @param type  the event type
     * @param earliest  the earlist time, in milliseconds since the epoch
     * @return  the list of events
     */
    @GetMapping({"/events/{type}/earliest/{earliest}"})
    @ResponseBody
    public List<Event> findByDateRange(@PathVariable("type") String type, @PathVariable("earliest") long earliest) {
        return this.findByDateRange(type, earliest, Instant.now().toEpochMilli());
    }

    /**
     * Returns the list of events from the earliest time to the latest time.  Times are milliseconds since the epoch.
     * The list is returned in reverse chronological order.
     * @param type  the event type
     * @param earliest  the earlist time, in milliseconds since the epoch
     * @return  the list of events
     */
    @GetMapping({"/events/{type}/earliest/{earliest}/latest/{latest}"})
    @ResponseBody
    public List<Event> findByDateRange(@PathVariable("type") String type, @PathVariable("earliest") long earliest, @PathVariable("latest") long latest) {
        Instant earliestTime = Instant.ofEpochMilli(earliest);
        Instant latestTime = Instant.ofEpochMilli(latest);
        return this.eventService.findEvents(type, earliestTime, latestTime);
    }

    /**
     * Returns the list of event type, order alphabetically.
     * @return  the list of event types
     */
    @GetMapping({"/types"})
    @ResponseBody
    public List<String> allEventTypes() {
        return this.eventService.allEventTypes();
    }

    /**
     * Adds the event.
     * @param event  the event to be added
     * @param ucBuilder  the uri component builder
     * @return the event that was added
     */
    @PostMapping({"/events"})
    public ResponseEntity<Event> addEvent(@RequestBody Event event, UriComponentsBuilder ucBuilder) {
        logger.info("received POST | event {}", event);
        HttpHeaders headers = new HttpHeaders();
        if (event != null && !event.isInvalid()) {
            boolean wasAdded = this.eventService.addEvent(event);
            if (wasAdded) {
                headers.setLocation(ucBuilder.path("/events/" + event.getType() + "/").build().toUri());
                return new ResponseEntity<>(headers, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Root redirect.
     * @param model  the model map
     * @return  the ModelAndView
     */
    @GetMapping
    public ModelAndView indexRedirect(ModelMap model) {
        return new ModelAndView("forward:/index.html", model);
    }
}

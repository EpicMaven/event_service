
package org.novalabs.event.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.novalabs.event.dao.EventDao;
import org.novalabs.event.dao.EventDaoException;
import org.novalabs.event.domain.Event;
import org.novalabs.event.domain.EventCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The EventService provides the core business logic of the Event Service.
 */
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventDao eventDao;

    /**
     * Creates an {@code EventService} with the dao to use for persistence.
     * @param eventDao  the DAO for event persistence
     */
    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    /**
     * Returns all event types.
     * @return  the event types.
     */
    public List<String> allEventTypes() {
        List types;
        try {
            types = this.eventDao.findAllTypes();
        } catch (Exception e) {
            logger.warn("Couldn't retrieve event types", e);
            types = Collections.emptyList();
        }

        return types;
    }

    /**
     * Adds the {@code Event} and returns whether successful.
     * @param event  the event
     * @return  whether successfully added
     */
    public boolean addEvent(Event event) {
        boolean wasAdded = false;

        try {
            wasAdded = this.eventDao.addEvent(event);
        } catch (Exception e) {
            logger.warn("Couldn't add event | event {}", event, e);
        }

        return wasAdded;
    }

    /**
     * Returns the latest {@code Event} for the event type.
     * @param eventType  the event type
     * @return  the event, of null if none found
     */
    @Nullable
    public Event latestEvent(String eventType) {
        Event event = null;

        try {
            event = this.eventDao.findLatestEvent(eventType);
        } catch (EventDaoException e) {
            logger.error("Couldn't retrieve findLatestEvent", e);
        }

        return event;
    }

    /**
     * Returns the list of latest events, one for each type.
     * @return  the list of latest events
     */
    public List<Event> latestEvents() {
        List latestEvents;
        try {
            latestEvents = this.eventDao.findLatestEvents();
        } catch (EventDaoException e) {
            latestEvents = Collections.emptyList();
            logger.error("Couldn't retrieve findLatestEvents", e);
        }

        return latestEvents;
    }

    /**
     * Return the list of events for a type.
     * @param eventType  the event type
     * @return  the list of events
     */
    public List<Event> findEvents(String eventType) {
        List events;
        try {
            events = this.eventDao.findEvents(eventType);
        } catch (EventDaoException e) {
            events = Collections.emptyList();
            logger.error("Couldn't retrieve findLatestEvent", e);
        }

        return events;
    }

    /**
     * Return the list of events for a type between earliestTime and latestTime, inclusive.
     * @param eventType  the event type
     * @param earliestTime earliest event time, inclusive
     * @param latestTime latest event time, inclusive
     * @return  the list of events
     */
    public List<Event> findEvents(String eventType, Instant earliestTime, Instant latestTime) {
        List events;
        try {
            events = this.eventDao.findEvents(eventType, earliestTime, latestTime);
        } catch (EventDaoException e) {
            events = Collections.emptyList();
            logger.error("Couldn't retrieve findLatestEvent | earliest {} | latest {}", earliestTime, latestTime, e);
        }

        return events;
    }

    /**
     * Returns the {@code EventCount} for the event type.
     * @param eventType  the event type
     * @return  the count of events
     */
    public EventCount countEvents(String eventType) {
        EventCount eventCount;
        try {
            long count = this.eventDao.countEvents(eventType);
            eventCount = new EventCount(eventType, count);
        } catch (EventDaoException e) {
            eventCount = null;
            logger.error("Couldn't retrieve countEvents", e);
        }

        return eventCount;
    }
}

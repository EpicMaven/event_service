
package org.novalabs.event.dao;

import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;
import org.novalabs.event.domain.Event;

/**
 * Data Access Object (DAO) for events.
 */
public interface EventDao {
    /**
     * Returns the list of events for the event type.  The list is returned in reverse chronological order.
     * @param eventType the event type
     * @return  the list of events
     * @throws EventDaoException  if the list couldn't be returned
     */
    List<Event> findEvents(String eventType) throws EventDaoException;

    /**
     * Returns the list of events for the event type, between the earliest and latest times, inclusive.  Times are
     * milliseconds since the epoch.  The list is returned in reverse chronological order.
     * @param eventType the event type
     * @param earliestTime  the earliest event time, inclusive
     * @param latestTime  the latest event time, inclusive
     * @return  the list of events
     * @throws EventDaoException  if the list couldn't be returned
     */
    List<Event> findEvents(String eventType, Instant earliestTime, Instant latestTime) throws EventDaoException;

    /**
     * Returns the list of all event types.
     * @return  the list of event types
     * @throws EventDaoException if the types couldn't be returned
     */
    List<String> findAllTypes() throws EventDaoException;

    /**
     * Returns the latest event for the event type.
     * @param eventType the event type
     * @return  the latest event, or null if none
     * @throws EventDaoException  if the event couldn't be returned
     */
    @Nullable
    Event findLatestEvent(String eventType) throws EventDaoException;

    /**
     * Returns the list of the latest event for each event type.
     * @return  the list of latest events
     * @throws EventDaoException  if the events couldn't be returned
     */
    List<Event> findLatestEvents() throws EventDaoException;

    /**
     * Returns the count of events for an event type.
     * @param eventType  the event type
     * @return  the count of events
     * @throws EventDaoException  if the count couldn't be determined
     */
    long countEvents(String eventType) throws EventDaoException;

    /**
     * Adds the {@code Event} and returns if successful.
     * @param event  the event to be added
     * @return  if added
     * @throws EventDaoException if an error occurred while adding the event
     */
    boolean addEvent(Event event) throws EventDaoException;
}

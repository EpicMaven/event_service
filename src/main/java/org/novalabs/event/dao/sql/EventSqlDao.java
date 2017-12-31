
package org.novalabs.event.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import org.novalabs.event.dao.EventDao;
import org.novalabs.event.dao.EventDaoException;
import org.novalabs.event.domain.Event;
import org.novalabs.event.domain.EventType;
import org.novalabs.event.domain.EventValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Event DAO for SQL data sources (databases).
 */
public class EventSqlDao implements EventDao {
    private static final String UNIQUE_TYPES = "SELECT DISTINCT type from event";
    private static final String ALL_EVENTS_SQL = "SELECT * from event WHERE type = ? ORDER BY time DESC";
    private static final String LATEST_EVENT_SQL = "SELECT * from event WHERE type = ? ORDER BY time DESC LIMIT 1";
    private static final String LATEST_EVENTS_SQL = "SELECT t1.uuid,t1.type,t1.value,t1.time FROM event t1  INNER JOIN (SELECT MAX(time) time,type FROM event GROUP BY type) AS t2 ON t1.type = t2.type AND t1.time = t2.time ORDER BY t1.type";
    private static final String FIND_EVENTS_IN_TIME = "SELECT * from event WHERE type = ? AND time >= ? AND time <= ? ORDER BY time DESC";
    private static final String COUNT_EVENTS = "SELECT count(*) from event WHERE type = ?";
    private static final String INSERT_EVENT = "INSERT INTO event (uuid,type,value,time) VALUES (?,?,?,?)";
    private static final Logger logger = LoggerFactory.getLogger(EventSqlDao.class);
    private final DataSource dataSource;
    private Connection connection;

    /**
     * Creates an {@code EventSqlDao} with the provided {@code DataSource}.
     * @param datasource  the data source to use
     */
    public EventSqlDao(DataSource datasource) {
        this.dataSource = datasource;
    }

    /**
     * Returns the list of all event types.
     * @return  the list of event types
     * @throws EventDaoException if the types couldn't be returned
     */
    public List<String> findAllTypes() throws EventDaoException {
        ArrayList types = new ArrayList();

        try {
            PreparedStatement preparedStatement = this.preparedStatement(UNIQUE_TYPES);
            ResultSet resultSet = preparedStatement.executeQuery();
            String typeName = null;

            while(resultSet.next()) {
                try {
                    typeName = resultSet.getString("type");
                    types.add(typeName);
                } catch (Exception e) {
                    logger.warn("Illegal EventType name in findAllTypes | name {}", typeName, e);
                }
            }

            return types;
        } catch (Exception e) {
            throw new EventDaoException("findAllTypes database call failed", e);
        }
    }

    /**
     * Returns the latest event for the event type.
     * @param eventType the event type
     * @return  the latest event, or null if none
     * @throws EventDaoException  if the event couldn't be returned
     */
    @Nullable
    public Event findLatestEvent(String eventType) throws EventDaoException {
        Event event = null;

        try {
            PreparedStatement preparedStatement = this.preparedStatement(LATEST_EVENT_SQL);
            preparedStatement.setString(1, eventType);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                event = this.buildEvent(resultSet);
            }

            return event;
        } catch (Exception e) {
            throw new EventDaoException("findLatestEvent database call failed", e);
        }
    }

    /**
     * Returns the list of the latest event for each event type.
     * @return  the list of latest events
     * @throws EventDaoException  if the events couldn't be returned
     */
    public List<Event> findLatestEvents() throws EventDaoException {
        ArrayList latestEvents = new ArrayList();

        try {
            PreparedStatement preparedStatement = this.preparedStatement(LATEST_EVENTS_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                latestEvents.add(this.buildEvent(resultSet));
            }

            return latestEvents;
        } catch (Exception e) {
            logger.warn("findLatestEvents database call failed | sql {}", LATEST_EVENTS_SQL);
            throw new EventDaoException("findLatestEvents database call failed", e);
        }
    }

    /**
     * Adds the {@code Event} and returns if successful.
     * @param event  the event to be added
     * @return  if added
     * @throws EventDaoException if an error occurred while adding the event
     */
    public boolean addEvent(Event event) throws EventDaoException {
        boolean wasAdded = false;

        try {
            PreparedStatement preparedStatement = this.preparedStatement(INSERT_EVENT);
            preparedStatement.setObject(1, event.getUuid());
            preparedStatement.setString(2, event.getType().getString());
            preparedStatement.setString(3, event.getValue().getString());
            preparedStatement.setTimestamp(4, Timestamp.from(event.getTime()));
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted == 1) {
                wasAdded = true;
            } else {
                logger.warn("Event NOT added to the database | event {}", event);
            }

            return wasAdded;
        } catch (Exception e) {
            throw new EventDaoException("findLatestEvent database call failed", e);
        }
    }

    /**
     * Returns the list of events for the event type.  The list is returned in reverse chronological order.
     * @param eventType the event type
     * @return  the list of events
     * @throws EventDaoException  if the list couldn't be returned
     */
    public List<Event> findEvents(String eventType) throws EventDaoException {
        ArrayList events = new ArrayList(30);

        try {
            PreparedStatement preparedStatement = this.preparedStatement(ALL_EVENTS_SQL);
            preparedStatement.setString(1, eventType);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                try {
                    events.add(this.buildEvent(resultSet));
                } catch (SQLException e) {
                    logger.warn("Couldn't deserialize Event from database", e);
                }
            }

            return events;
        } catch (Exception e) {
            String msg = "findEvents database call failed";
            logger.error(msg);
            throw new EventDaoException(msg, e);
        }
    }

    /**
     * Returns the list of events for the event type, between the earliest and latest times, inclusive.  Times are
     * milliseconds since the epoch.  The list is returned in reverse chronological order.
     * @param eventType the event type
     * @param earliestTime  the earliest event time, inclusive
     * @param latestTime  the latest event time, inclusive
     * @return  the list of events
     * @throws EventDaoException  if the list couldn't be returned
     */
    public List<Event> findEvents(String eventType, Instant earliestTime, Instant latestTime) throws EventDaoException {
        ArrayList events = new ArrayList(30);

        try {
            PreparedStatement preparedStatement = this.preparedStatement(FIND_EVENTS_IN_TIME);
            preparedStatement.setString(1, eventType);
            preparedStatement.setTimestamp(2, Timestamp.from(earliestTime));
            preparedStatement.setTimestamp(3, Timestamp.from(latestTime));
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                try {
                    events.add(this.buildEvent(resultSet));
                } catch (Exception e) {
                    logger.warn("Couldn't deserialize Event from database", e);
                }
            }

            return events;
        } catch (Exception e) {
            String msg = "findEvents database call failed | oldest " + earliestTime + " | newest " + latestTime;
            logger.error(msg);
            throw new EventDaoException(msg, e);
        }
    }

    /**
     * Returns the count of events for an event type.
     * @param eventType  the event type
     * @return  the count of events
     * @throws EventDaoException  if the count couldn't be determined
     */
    public long countEvents(String eventType) throws EventDaoException {
        long count = -1L;

        try {
            PreparedStatement preparedStatement = this.preparedStatement(COUNT_EVENTS);
            preparedStatement.setString(1, eventType);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                try {
                    count = resultSet.getLong(1);
                } catch (Exception e) {
                    logger.warn("Couldn't deserialize Event from database", e);
                }
            }

            return count;
        } catch (Exception e) {
            String msg = "countEvents database call failed | type " + eventType;
            logger.error(msg);
            throw new EventDaoException(msg, e);
        }
    }

    private Event buildEvent(ResultSet resultSet) throws SQLException {
        UUID uuid = resultSet.getObject("uuid", UUID.class);
        EventType type = new EventType(resultSet.getString("type"));
        EventValue value = new EventValue(resultSet.getString("value"));
        Instant time = resultSet.getTimestamp("time").toInstant();
        return new Event(uuid, type, value, time);
    }

    private PreparedStatement preparedStatement(String sql) throws SQLException {
        return this.connection().prepareStatement(sql);
    }

    private Connection connection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = this.dataSource.getConnection();
        }

        return this.connection;
    }
}


package org.novalabs.event.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.Preconditions;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Event represents an event in time.  Each event has a type and unique ID.
 */
public class Event {
    public static final EventType INVALID_TYPE = new EventType("---INVALID-TYPE---");
    public static final EventValue INVALID_VALUE = new EventValue("---INVALID-VALUE---");
    private UUID uuid;
    private EventType type;
    private EventValue value;
    private long epochMillis;

    /**
     * Creates a event with an invalid type and invalid value.  This constructor is used primarily for Spring auto
     * construction.
     */
    public Event() {
        this(UUID.randomUUID(), INVALID_TYPE, INVALID_VALUE, System.currentTimeMillis());
    }

    /**
     * Creates an event with the uuid, tyoe, value, and time.
     * @param uuid  the unique id
     * @param type  the event type
     * @param value  the event value
     * @param time  the time of the event
     */
    public Event(UUID uuid, EventType type, EventValue value, Instant time) {
        this(uuid, type, value, time.toEpochMilli());
    }

    /**
     * Creates an event with the uuid, tyoe, value, and time in epoch milliseconds.
     * @param uuid  the unique id
     * @param type  the event type
     * @param value  the event value
     * @param epochMillis  the time of the event
     */
    public Event(UUID uuid, EventType type, EventValue value, long epochMillis) {
        Preconditions.checkArgument(uuid != null);
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(epochMillis >= 0L);
        this.uuid = uuid;
        this.type = type;
        this.value = value;
        this.epochMillis = epochMillis;
    }

    /**
     * Returns the unique id.
     * @return  the uuid
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Sets the uuid.
     * @param uuid  the uuid.
     */
    public void setUuid(UUID uuid) {
        Preconditions.checkArgument(uuid != null);
        this.uuid = uuid;
    }

    /**
     * Returns the event type
     * @return  the event type
     */
    // serialize the JSON as the string from toString method of the object
    @JsonSerialize(using = ToStringSerializer.class)
    public EventType getType() {
        return this.type;
    }

    /**
     * Set the event type
     * @param type  the event type
     */
    public void setType(EventType type) {
        Preconditions.checkNotNull(type);
        this.type = type;
    }

    /**
     * Returns the event value
     * @return  the value
     */
    // serialize the JSON as the string from toString method of the object
    @JsonSerialize(using = ToStringSerializer.class)
    public EventValue getValue() {
        return this.value;
    }

    /**
     * Sets the event value
     * @param value  the event value
     */
    public void setValue(EventValue value) {
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    /**
     * Returns the event time in milliseconds from the epoch.
     * @return  the event time in epoch milliseconds
     */
    public long getEpochMillis() {
        return this.epochMillis;
    }

    /**
     * Set the event time to epochMillis
     * @param epochMillis  the event time
     */
    public void setEpochMillis(long epochMillis) {
        Preconditions.checkArgument(epochMillis >= 0L);
        this.epochMillis = epochMillis;
    }

    /**
     * Returns the event time as an {@code Instant}
     * @return  the event time
     */
    @JsonFormat(
            shape = Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            timezone = "UTC"
    )
    public Instant getTime() {
        return Instant.ofEpochMilli(this.epochMillis);
    }

    /**
     * Returns the event as a string
     * @return  the string
     */
    public String toString() {
        return "uuid '" + this.uuid + "', type '" + this.type + "', value '" + this.value + "', epochMillis '" + this.getTime() + "'";
    }

    /**
     * Returns whether the event is invalid.  Default constructor objects are invalid.
     * @return  whether the event is invalid
     */
    @JsonIgnore
    public boolean isInvalid() {
        return this.type.equals(INVALID_TYPE) && this.value.equals(INVALID_VALUE);
    }

    /**
     * Returns whether the two event objects are equivalent.
     * @param obj  the object to compare
     * @return  whether equal
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj instanceof Event) {
            Event rhs = (Event)obj;
            return Objects.equals(this.uuid, rhs.uuid) && Objects.equals(this.type, rhs.type) && Objects.equals(this.value, rhs.value) && Objects.equals(this.epochMillis, rhs.epochMillis);
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code of the event.
     * @return  the hash code
     */
    public int hashCode() {
        return Objects.hash(new Object[]{this.uuid, this.type, this.value, this.epochMillis});
    }
}

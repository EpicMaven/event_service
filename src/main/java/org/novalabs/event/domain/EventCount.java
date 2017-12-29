
package org.novalabs.event.domain;

/**
 * Represents the count of events.
 */
public class EventCount {
    private String type;
    private long count;

    /**
     * Default constructor, creates an invalid object.  This is used primarily by Spring.
     */
    public EventCount() {
        this("--invalid--", -1L);
    }

    /**
     * Create an {@code EventCount} with the type and count.
     * @param type  the event type
     * @param count  the count
     */
    public EventCount(String type, long count) {
        this.type = type;
        this.count = count;
    }

    /**
     * Returns the event type.
     * @return  the event type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the event type
     * @param type  the event type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the event count
     * @return  the count
     */
    public long getCount() {
        return this.count;
    }

    /**
     * Sets the event count.
     * @param count
     */
    public void setCount(long count) {
        this.count = count;
    }
}


package org.novalabs.event.domain;

import org.incava.ijdk.lang.Str;

/**
 * Represents the type of Event.  Event type has a minimum of 1 character and maximum of 100 characters.
 */
public class EventType extends Str {
    public static final int MIN_LEGNTH = 1;
    public static final int MAX_LEGNTH = 100;

    /**
     * Creates an {@code EventType} of type.
     * @param type  the event type
     */
    public EventType(String type) {
        super(type);
        this.checkType();
    }

    /**
     * Creates an {@code EventType} of type.
     * @param type  the event type
     */
    public EventType(char type) {
        super(type);
        this.checkType();
    }

    /**
     * Creates an {@code EventType} of str, repeated num times.
     * @param str the base type
     * @param num the number of times to repeat str
     */
    public EventType(String str, int num) {
        super(str, num);
        this.checkType();
    }

    /**
     * Checks type and throws IllegalArgumentException if it fails any check.
     */
    private final void checkType() {
        if (this.isEmpty() || this.length() < MIN_LEGNTH || this.length() > MAX_LEGNTH) {
            throw new IllegalArgumentException("EventType must be between " + MIN_LEGNTH + " and " +
                                                       MAX_LEGNTH + " characters");
        }
    }
}

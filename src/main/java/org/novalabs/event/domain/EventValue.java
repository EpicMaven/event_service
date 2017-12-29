
package org.novalabs.event.domain;

import org.incava.ijdk.lang.Str;

/**
 * Represents the value of Event.  Event value has a minimum of 1 character and maximum of 255 characters.
 */
public class EventValue extends Str {
    public static final int MIN_LEGNTH = 1;
    public static final int MAX_LEGNTH = 255;

    /**
     * Creates an {@code EventValue} of value.
     * @param value  the event value
     */
    public EventValue(String value) {
        super(value);
        this.checkValue();
    }

    /**
     * Creates an {@code EventValue} of value.
     * @param value  the event value
     */
    public EventValue(char value) {
        super(value);
        this.checkValue();
    }

    /**
     * Creates an {@code EventValue} of str, repeated num times.
     * @param str the base value
     * @param num the number of times to repeat str
     */
    public EventValue(String str, int num) {
        super(str, num);
        this.checkValue();
    }

    /**
     * Checks value and throws IllegalArgumentException if it fails any check.
     */
    private final void checkValue() {
        if (this.isEmpty() || this.length() < MIN_LEGNTH || this.length() > MAX_LEGNTH) {
            throw new IllegalArgumentException("EventValue must be between " + MIN_LEGNTH + " and " +
                                                       MAX_LEGNTH + " characters");
        }
    }
}


package org.novalabs.event.dao;

/**
 * EventDao Exception.
 */
public class EventDaoException extends Exception {
    public EventDaoException() {
    }

    public EventDaoException(String message) {
        super(message);
    }

    public EventDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventDaoException(Throwable cause) {
        super(cause);
    }

    protected EventDaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package org.jeecg.modules.qwert.conn.qudong.sero.messaging;

import java.io.IOException;

/**
 * <p>TimeoutException class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class TimeoutException extends IOException {
    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for TimeoutException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public TimeoutException(String message) {
        super(message);
    }
}

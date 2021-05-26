package org.jeecg.modules.qwert.qudong.sero.messaging;

import java.io.IOException;

/**
 * <p>WaitingRoomException class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class WaitingRoomException extends IOException {
    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for WaitingRoomException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public WaitingRoomException(String message) {
        super(message);
    }
}

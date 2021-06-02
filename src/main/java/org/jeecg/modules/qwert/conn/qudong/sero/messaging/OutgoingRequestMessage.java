package org.jeecg.modules.qwert.conn.qudong.sero.messaging;


/**
 * <p>OutgoingRequestMessage interface.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public interface OutgoingRequestMessage extends OutgoingMessage {
    /**
     * Whether the request is expecting a response or not.
     *
     * @return true if a response is expected, false otherwise.
     */
    boolean expectsResponse();
}

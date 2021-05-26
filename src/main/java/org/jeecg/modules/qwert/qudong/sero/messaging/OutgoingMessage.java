package org.jeecg.modules.qwert.qudong.sero.messaging;

/**
 * <p>OutgoingMessage interface.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public interface OutgoingMessage {
    /**
     * Return the byte array representing the serialization of the request.
     *
     * @return byte array representing the serialization of the request
     */
    byte[] getMessageData();
    byte[] getMessageData2(int r);
}

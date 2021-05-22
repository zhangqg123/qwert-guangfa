package org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging;

/**
 * <p>MessagingExceptionHandler interface.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public interface MessagingExceptionHandler {
    /**
     * <p>receivedException.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public void receivedException(Exception e);
}

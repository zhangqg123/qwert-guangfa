package org.jeecg.modules.qwert.conn.qudong.sero.messaging;

/**
 * <p>DefaultMessagingExceptionHandler class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class DefaultMessagingExceptionHandler implements MessagingExceptionHandler {
    /** {@inheritDoc} */
    public void receivedException(Exception e) {
        e.printStackTrace();
    }
}

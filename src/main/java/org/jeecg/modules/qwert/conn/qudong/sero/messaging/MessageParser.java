package org.jeecg.modules.qwert.conn.qudong.sero.messaging;

import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * Interface defining methods that are called when data arrives in the connection.
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public interface MessageParser {
    /**
     * Attempt to parse a message out of the queue. Data in the queue may be discarded if it is unusable (i.e. a start
     * indicator is not found), but otherwise if a message is not found due to the data being incomplete, the method
     * should return null. As additional data arrives, it will be appended to the queue and this method will be called
     * again.
     *
     * Implementations should not modify the queue unless it is safe to do so. No copy of the data is made before
     * calling this method.
     *
     * @param queue
     *            the queue from which to access data for the creation of the message
     * @return the message if one was able to be created, or null otherwise.
     * @throws java.lang.Exception
     *             if the data in the queue is sufficient to construct a message, but the message data is invalid, this
     *             method must throw an exception, or it will keep getting the same data.
     */
    IncomingMessage parseMessage(ByteQueue queue) throws Exception;
}

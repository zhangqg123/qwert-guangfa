package org.jeecg.modules.qwert.conn.dianzong.sero.messaging;


/**
 * <p>WaitingRoomKeyFactory interface.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public interface WaitingRoomKeyFactory {
    /**
     * <p>createWaitingRoomKey.</p>
     *
     * @param request a {@link org.jeecg.modules.qwert.conn.dianzong.sero.messaging.OutgoingRequestMessage} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.sero.messaging.WaitingRoomKey} object.
     */
    WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request);

    /**
     * <p>createWaitingRoomKey.</p>
     *
     * @param response a {@link org.jeecg.modules.qwert.conn.dianzong.sero.messaging.IncomingResponseMessage} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.sero.messaging.WaitingRoomKey} object.
     */
    WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response);
}

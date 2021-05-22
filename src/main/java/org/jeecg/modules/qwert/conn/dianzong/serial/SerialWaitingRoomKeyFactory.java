package org.jeecg.modules.qwert.conn.dianzong.serial;

import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.IncomingResponseMessage;
import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.OutgoingRequestMessage;
import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.WaitingRoomKey;
import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.WaitingRoomKeyFactory;

/**
 * <p>SerialWaitingRoomKeyFactory class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class SerialWaitingRoomKeyFactory implements WaitingRoomKeyFactory {
    private static final Sync sync = new Sync();

    /** {@inheritDoc} */
    @Override
    public WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request) {
        return sync;
    }

    /** {@inheritDoc} */
    @Override
    public WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response) {
        return sync;
    }

    static class Sync implements WaitingRoomKey {
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return true;
        }
    }
}

package org.jeecg.modules.qwert.conn.qudong.ip.encap;

import org.jeecg.modules.qwert.conn.qudong.ip.IpMessage;
import org.jeecg.modules.qwert.conn.qudong.msg.QwertMessage;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadM7000Request;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadM7000Response;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.IncomingResponseMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.OutgoingRequestMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.WaitingRoomKey;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.WaitingRoomKeyFactory;

/**
 * <p>EncapWaitingRoomKeyFactory class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class EncapWaitingRoomKeyFactory implements WaitingRoomKeyFactory {
    private int sid7000D=0;
    /** {@inheritDoc} */
    @Override
    public WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request) {
        return createWaitingRoomKey(((IpMessage) request).getModbusMessage());
    }

    /** {@inheritDoc} */
    @Override
    public WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response) {
        return createWaitingRoomKey(((IpMessage) response).getModbusMessage());
    }

    /**
     * <p>createWaitingRoomKey.</p>
     *
     * @param msg a {@link org.jeecg.modules.qwert.conn.qudong.msg.QwertMessage} object.
     * @return a {@link org.jeecg.modules.qwert.conn.qudong.sero.messaging.WaitingRoomKey} object.
     */
    public WaitingRoomKey createWaitingRoomKey(QwertMessage msg) {
        int slaveId = msg.getSlaveId();
        if(msg instanceof ReadM7000Request){
            sid7000D=msg.getSlaveId();
        }
        if(msg instanceof ReadM7000Response){
            slaveId=sid7000D;
            sid7000D=0;
        }

        return new EncapWaitingRoomKey(slaveId, msg.getFunctionCode());
    }

    class EncapWaitingRoomKey implements WaitingRoomKey {
        private final int slaveId;
        private final byte functionCode;

        public EncapWaitingRoomKey(int slaveId, byte functionCode) {
            this.slaveId = slaveId;
            this.functionCode = functionCode;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + functionCode;
            result = prime * result + slaveId;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EncapWaitingRoomKey other = (EncapWaitingRoomKey) obj;
            if (functionCode != other.functionCode)
                return false;
            if (slaveId != other.slaveId)
                return false;
            return true;
        }
    }
}

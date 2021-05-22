package org.jeecg.modules.qwert.conn.modbus4j.source.ip.encap;

import org.jeecg.modules.qwert.conn.modbus4j.source.ip.IpMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ModbusMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.IncomingResponseMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.OutgoingRequestMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.WaitingRoomKey;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.WaitingRoomKeyFactory;

/**
 * <p>EncapWaitingRoomKeyFactory class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class EncapWaitingRoomKeyFactory implements WaitingRoomKeyFactory {
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
     * @param msg a {@link ModbusMessage} object.
     * @return a {@link WaitingRoomKey} object.
     */
    public WaitingRoomKey createWaitingRoomKey(ModbusMessage msg) {
        return new EncapWaitingRoomKey(msg.getSlaveId(), msg.getFunctionCode());
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

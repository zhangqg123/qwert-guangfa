package org.jeecg.modules.qwert.conn.modbus4j.source.ip.xa;

import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ModbusMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.IncomingResponseMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.OutgoingRequestMessage;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.WaitingRoomKey;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.WaitingRoomKeyFactory;

/**
 * <p>XaWaitingRoomKeyFactory class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class XaWaitingRoomKeyFactory implements WaitingRoomKeyFactory {
    /** {@inheritDoc} */
    @Override
    public WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request) {
        return createWaitingRoomKey((XaMessage) request);
    }

    /** {@inheritDoc} */
    @Override
    public WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response) {
        return createWaitingRoomKey((XaMessage) response);
    }

    /**
     * <p>createWaitingRoomKey.</p>
     *
     * @param msg a {@link XaMessage} object.
     * @return a {@link WaitingRoomKey} object.
     */
    public WaitingRoomKey createWaitingRoomKey(XaMessage msg) {
        return new XaWaitingRoomKey(msg.getTransactionId(), msg.getModbusMessage());
    }

    class XaWaitingRoomKey implements WaitingRoomKey {
        private final int transactionId;
        private final int slaveId;
        private final byte functionCode;

        public XaWaitingRoomKey(int transactionId, ModbusMessage msg) {
            this.transactionId = transactionId;
            this.slaveId = msg.getSlaveId();
            this.functionCode = msg.getFunctionCode();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + functionCode;
            result = prime * result + slaveId;
            result = prime * result + transactionId;
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
            XaWaitingRoomKey other = (XaWaitingRoomKey) obj;
            if (functionCode != other.functionCode)
                return false;
            if (slaveId != other.slaveId)
                return false;
            if (transactionId != other.transactionId)
                return false;
            return true;
        }
    }
}

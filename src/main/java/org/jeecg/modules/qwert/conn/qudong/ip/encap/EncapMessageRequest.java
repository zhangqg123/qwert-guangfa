/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2011 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jeecg.modules.qwert.conn.qudong.ip.encap;

import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.msg.QwertRequest;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.IncomingRequestMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.OutgoingRequestMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>EncapMessageRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class EncapMessageRequest extends EncapMessage implements OutgoingRequestMessage, IncomingRequestMessage {
    static EncapMessageRequest createEncapMessageRequest(ByteQueue queue) throws QudongTransportException {
        // Create the modbus response.
//        QwertRequest request = QwertRequest.createQwertRequest(queue);
//        byte code = queue.peek(0);
//        ByteQueue msgQueue = QwertAsciiUtils.getUnDianzongMessage(queue);
//        msgQueue.push(code);
        QwertRequest request = QwertRequest.createQwertRequest(queue);
        EncapMessageRequest encapRequest = new EncapMessageRequest(request);

        // Check the CRC
 //       QwertUtils.checkCRC(encapRequest.qwertMessage, queue);

        return encapRequest;
    }

    /**
     * <p>Constructor for EncapMessageRequest.</p>
     *
     * @param modbusRequest a {@link QwertRequest} object.
     */
    public EncapMessageRequest(QwertRequest modbusRequest) {
        super(modbusRequest);
    }

    /** {@inheritDoc} */
    @Override
    public boolean expectsResponse() {
        return qwertMessage.getSlaveId() != 0;
    }

    /**
     * <p>getModbusRequest.</p>
     *
     * @return a {@link QwertRequest} object.
     */
    public QwertRequest getModbusRequest() {
        return (QwertRequest) qwertMessage;
    }

}

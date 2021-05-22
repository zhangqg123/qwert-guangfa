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
package org.jeecg.modules.qwert.conn.dianzong.serial.dianzong;

import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.msg.QwertMessage;
import org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest;
import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.IncomingRequestMessage;
import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.OutgoingRequestMessage;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue;

/**
 * <p>DianzongMessageRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class DianzongMessageRequest extends DianzongMessage implements OutgoingRequestMessage, IncomingRequestMessage {
    static DianzongMessageRequest createDianzongMessageRequest(ByteQueue queue) throws DianzongTransportException {
        ByteQueue msgQueue = getUnDianzongMessage(queue);
        QwertRequest request = QwertRequest.createQwertRequest(msgQueue);
        DianzongMessageRequest dianzongRequest = new DianzongMessageRequest(request);

        // Return the data.
        return dianzongRequest;
    }

    /**
     * <p>Constructor for DianzongMessageRequest.</p>
     *
     * @param QwertMessage a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertMessage} object.
     */
    public DianzongMessageRequest(QwertMessage QwertMessage) {
        super(QwertMessage);
    }

    /** {@inheritDoc} */
    @Override
    public boolean expectsResponse() {
        return QwertMessage.getSlaveId() != 0;
    }

    /**
     * <p>getQwertRequest.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest} object.
     */
    public QwertRequest getQwertRequest() {
        return (QwertRequest) QwertMessage;
    }
}

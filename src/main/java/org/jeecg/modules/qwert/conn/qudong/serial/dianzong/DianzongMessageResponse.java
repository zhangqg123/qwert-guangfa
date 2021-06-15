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
package org.jeecg.modules.qwert.conn.qudong.serial.dianzong;

import org.jeecg.modules.qwert.conn.qudong.base.QwertAsciiUtils;
import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.msg.QwertMessage;
import org.jeecg.modules.qwert.conn.qudong.msg.QwertResponse;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.IncomingResponseMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.OutgoingResponseMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>DianzongMessageResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class DianzongMessageResponse extends DianzongMessage implements OutgoingResponseMessage, IncomingResponseMessage {
    static DianzongMessageResponse createDianzongMessageResponse(ByteQueue queue) throws QudongTransportException {
//        ByteQueue msgQueue = QwertAsciiUtils.getUnDianzongMessage(queue);
        QwertResponse response = QwertResponse.createQwertResponse(queue);
        DianzongMessageResponse dianzongResponse = new DianzongMessageResponse(response);

        // Return the data.
        return dianzongResponse;
    }

    /**
     * <p>Constructor for DianzongMessageResponse.</p>
     *
     * @param QwertMessage a {@link org.jeecg.modules.qwert.conn.qudong.msg.QwertMessage} object.
     */
    public DianzongMessageResponse(QwertMessage QwertMessage) {
        super(QwertMessage);
    }

    /**
     * <p>getQwertResponse.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.conn.qudong.msg.QwertResponse} object.
     */
    public QwertResponse getQwertResponse() {
        return (QwertResponse) qwertMessage;
    }
}

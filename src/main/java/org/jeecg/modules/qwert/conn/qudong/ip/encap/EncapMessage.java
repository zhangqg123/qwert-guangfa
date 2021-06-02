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

import org.jeecg.modules.qwert.conn.qudong.base.QwertAsciiUtils;
import org.jeecg.modules.qwert.conn.qudong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.qudong.ip.IpMessage;
import org.jeecg.modules.qwert.conn.qudong.msg.*;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadM7000Request;
import org.jeecg.modules.qwert.conn.qudong.msg.ReadM7000Response;
import org.jeecg.modules.qwert.conn.qudong.msg.delta.ReadDeltaRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.delta.ReadDeltaResponse;
import org.jeecg.modules.qwert.conn.qudong.msg.kstar.ReadKstarRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.kstar.ReadKstarResponse;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>EncapMessage class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class EncapMessage extends IpMessage {
    /**
     * <p>Constructor for EncapMessage.</p>
     *
     * @param qwertMessage a {@link org.jeecg.modules.qwert.conn.qudong.msg.QwertMessage} object.
     */
    public EncapMessage(QwertMessage qwertMessage) {
        super(qwertMessage);
    }

    /**
     * <p>getMessageData.</p>
     *
     * @return an array of {@link byte} objects.
     */
    public byte[] getMessageData() {
        ByteQueue msgQueue = new ByteQueue();

        // Write the particular message.
        qwertMessage.write(msgQueue);

        // Write the CRC
        QwertUtils.pushShort(msgQueue, QwertUtils.calculateCRC(qwertMessage));

        // Return the data.
        return msgQueue.popAll();
    }

    public byte[] getMessageData2(int r) {
        ByteQueue queue = new ByteQueue();
        qwertMessage.write2(queue,r);
        byte[] ret = null;
        if (qwertMessage instanceof ReadDianzongRequest || qwertMessage instanceof ReadDianzongResponse) {
            ret = QwertAsciiUtils.getAsciiData(queue, r);
        }
        if (qwertMessage instanceof ReadM7000Request || qwertMessage instanceof ReadM7000Response) {
            ret = QwertAsciiUtils.get7000AsciiData(queue, r);
        }
        if (qwertMessage instanceof ReadKstarRequest || qwertMessage instanceof ReadKstarResponse) {
            ret = QwertAsciiUtils.getKstarAsciiData(queue, r);
        }
        if (qwertMessage instanceof ReadDeltaRequest || qwertMessage instanceof ReadDeltaResponse) {
            ret = QwertAsciiUtils.getDeltaAsciiData(queue, r);
//            ret = queue.popAll();
        }
        return ret;
    }
    
}

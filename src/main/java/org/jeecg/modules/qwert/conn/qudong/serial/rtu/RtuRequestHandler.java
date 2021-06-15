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
package org.jeecg.modules.qwert.conn.qudong.serial.rtu;

import org.jeecg.modules.qwert.conn.qudong.QwertSlaveSet;
import org.jeecg.modules.qwert.conn.qudong.base.BaseRequestHandler;
import org.jeecg.modules.qwert.conn.qudong.msg.QwertRequest;
import org.jeecg.modules.qwert.conn.qudong.msg.QwertResponse;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.IncomingRequestMessage;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.OutgoingResponseMessage;

/**
 * <p>RtuRequestHandler class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class RtuRequestHandler extends BaseRequestHandler {
    /**
     * <p>Constructor for RtuRequestHandler.</p>
     *
     * @param slave a {@link QwertSlaveSet} object.
     */
    public RtuRequestHandler(QwertSlaveSet slave) {
        super(slave);
    }

    /** {@inheritDoc} */
    public OutgoingResponseMessage handleRequest(IncomingRequestMessage req) throws Exception {
        RtuMessageRequest dianzongRequest = (RtuMessageRequest) req;
        QwertRequest request = dianzongRequest.getQwertRequest();
        QwertResponse response = handleRequestImpl(request);
        if (response == null)
            return null;
        return new RtuMessageResponse(response);
    }
}

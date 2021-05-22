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

import org.jeecg.modules.qwert.conn.dianzong.QwertSlaveSet;
import org.jeecg.modules.qwert.conn.dianzong.base.BaseRequestHandler;
import org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest;
import org.jeecg.modules.qwert.conn.dianzong.msg.QwertResponse;
import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.IncomingRequestMessage;
import org.jeecg.modules.qwert.conn.dianzong.sero.messaging.OutgoingResponseMessage;

/**
 * <p>DianzongRequestHandler class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class DianzongRequestHandler extends BaseRequestHandler {
    /**
     * <p>Constructor for DianzongRequestHandler.</p>
     *
     * @param slave a {@link org.jeecg.modules.qwert.conn.dianzong.QwertSlaveSet} object.
     */
    public DianzongRequestHandler(QwertSlaveSet slave) {
        super(slave);
    }

    /** {@inheritDoc} */
    public OutgoingResponseMessage handleRequest(IncomingRequestMessage req) throws Exception {
        DianzongMessageRequest dianzongRequest = (DianzongMessageRequest) req;
        QwertRequest request = dianzongRequest.getQwertRequest();
        QwertResponse response = handleRequestImpl(request);
        if (response == null)
            return null;
        return new DianzongMessageResponse(response);
    }
}

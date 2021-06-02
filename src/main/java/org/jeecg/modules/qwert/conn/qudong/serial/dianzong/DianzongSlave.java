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

import java.io.IOException;

import org.jeecg.modules.qwert.conn.qudong.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.qudong.serial.SerialPortWrapper;
import org.jeecg.modules.qwert.conn.qudong.serial.SerialSlave;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.MessageControl;

/**
 * <p>DianzongSlave class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class DianzongSlave extends SerialSlave {
    private MessageControl conn;

    /**
     * <p>Constructor for DianzongSlave.</p>
     *
     * @param wrapper a {@link org.jeecg.modules.qwert.conn.qudong.serial.SerialPortWrapper} object.
     */
    public DianzongSlave(SerialPortWrapper wrapper) {
        super(wrapper);
    }

    /** {@inheritDoc} */
    @Override
    public void start() throws ModbusInitException {
        super.start();

        DianzongMessageParser DianzongMessageParser = new DianzongMessageParser(false);
        DianzongRequestHandler dianzongRequestHandler = new DianzongRequestHandler(this);

        conn = new MessageControl();
        conn.setExceptionHandler(getExceptionHandler());

        try {
            conn.start(transport, DianzongMessageParser, dianzongRequestHandler, null);
            transport.start("Qwert ASCII slave");
        }
        catch (IOException e) {
            throw new ModbusInitException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        conn.close();
        super.stop();
    }
}

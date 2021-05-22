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
package org.jeecg.modules.qwert.conn.modbus4j.source.serial.ascii;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.modbus4j.source.exception.ModbusTransportException;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ModbusRequest;
import org.jeecg.modules.qwert.conn.modbus4j.source.msg.ModbusResponse;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.SerialMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.SerialPortWrapper;
import org.jeecg.modules.qwert.conn.modbus4j.source.serial.SerialWaitingRoomKeyFactory;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.MessageControl;
import org.jeecg.modules.qwert.conn.modbus4j.source.sero.messaging.StreamTransport;

/**
 * <p>AsciiMaster class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class AsciiMaster extends SerialMaster {
    private final Log LOG = LogFactory.getLog(SerialMaster.class);

    private MessageControl conn;

    /**
     * <p>Constructor for AsciiMaster.</p>
     *
     * Default to validating the slave id in responses
     *
     * @param wrapper a {@link SerialPortWrapper} object.
     */
    public AsciiMaster(SerialPortWrapper wrapper) {
        super(wrapper, true);
    }

    /**
     *
     * @param wrapper a {@link SerialPortWrapper} object.
     * @param validateResponse - confirm that requested slave id is the same in the response
     */
    public AsciiMaster(SerialPortWrapper wrapper, boolean validateResponse) {
        super(wrapper, validateResponse);
    }

    /** {@inheritDoc} */
    @Override
    public void init() throws ModbusInitException {
        try {
            openConnection(null);
        }
        catch (Exception e) {
            throw new ModbusInitException(e);
        }
        initialized = true;
    }

    @Override
    protected void openConnection(MessageControl toClose) throws Exception {
        super.openConnection(toClose);
        AsciiMessageParser asciiMessageParser = new AsciiMessageParser(true);
        this.conn = getMessageControl();
        this.conn.start(transport, asciiMessageParser, null, new SerialWaitingRoomKeyFactory());
        if (getePoll() == null) {
            ((StreamTransport) transport).start("Modbus ASCII master");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {
        closeMessageControl(conn);
        super.close();
        initialized = false;
    }

    /** {@inheritDoc} */
    @Override
    public ModbusResponse sendImpl(ModbusRequest request) throws ModbusTransportException {
        // Wrap the modbus request in an ascii request.
        AsciiMessageRequest asciiRequest = new AsciiMessageRequest(request);

        // Send the request to get the response.
        AsciiMessageResponse asciiResponse;
        try {
            asciiResponse = (AsciiMessageResponse) conn.send(asciiRequest);
            if (asciiResponse == null)
                return null;
            return asciiResponse.getModbusResponse();
        }
        catch (Exception e) {
            try {
                LOG.debug("Connection may have been reset. Attempting to re-open.");
                openConnection(conn);
                asciiResponse = (AsciiMessageResponse) conn.send(asciiRequest);
                if (asciiResponse == null)
                    return null;
                return asciiResponse.getModbusResponse();
            }catch(Exception e2) {
                closeConnection(conn);
                LOG.debug("Failed to re-connect", e);
                throw new ModbusTransportException(e2, request.getSlaveId());
            }
        }
    }
}

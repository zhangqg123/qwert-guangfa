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
package org.jeecg.modules.qwert.conn.qudong.serial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jeecg.modules.qwert.conn.qudong.QwertSlaveSet;
import org.jeecg.modules.qwert.conn.qudong.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.qudong.sero.messaging.StreamTransport;

/**
 * <p>Abstract SerialSlave class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class SerialSlave extends QwertSlaveSet {

	private final Log LOG = LogFactory.getLog(SerialSlave.class);
	
    // Runtime fields
    private SerialPortWrapper wrapper;
    protected StreamTransport transport;

    /**
     * <p>Constructor for SerialSlave.</p>
     *
     * @param wrapper a {@link org.jeecg.modules.qwert.conn.qudong.serial.SerialPortWrapper} object.
     */
    public SerialSlave(SerialPortWrapper wrapper) {
    	this.wrapper = wrapper;
    }

    /** {@inheritDoc} */
    @Override
    public void start() throws ModbusInitException {
        try {
        	
        	wrapper.open();

            transport = new StreamTransport(wrapper.getInputStream(), wrapper.getOutputStream());
        }
        catch (Exception e) {
            throw new ModbusInitException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        try {
			wrapper.close();
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		}
    }
}

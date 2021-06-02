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
package org.jeecg.modules.qwert.conn.qudong.msg;

import org.jeecg.modules.qwert.conn.qudong.ProcessImage;
import org.jeecg.modules.qwert.conn.qudong.Qwert;
import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.sero.ShouldNeverHappenException;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>ExceptionRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ExceptionRequest extends QwertRequest {
    private final byte functionCode;
    private final byte exceptionCode;

    /**
     * <p>Constructor for ExceptionRequest.</p>
     *
     * @param slaveId a int.
     * @param functionCode a byte.
     * @param exceptionCode a byte.
     * @throws org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException if any.
     */
    public ExceptionRequest(int slaveId, byte functionCode, byte exceptionCode) throws QudongTransportException {
        super(slaveId);
        this.functionCode = functionCode;
        this.exceptionCode = exceptionCode;
    }

    /** {@inheritDoc} */
    @Override
    public void validate(Qwert modbus) {
        // no op
    }

    /** {@inheritDoc} */
    @Override
    protected void writeRequest(ByteQueue queue) {
        throw new ShouldNeverHappenException("wha");
    }

    /** {@inheritDoc} */
    @Override
    protected void readRequest(ByteQueue queue) {
        queue.clear();
    }

    @Override
    public QwertResponse getResponseInstance(int slaveId) throws QudongTransportException {
        return new ExceptionResponse(slaveId, functionCode, exceptionCode);
    }

    @Override
    public QwertResponse handleImpl(ProcessImage processImage) throws QudongTransportException {
        return getResponseInstance(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return functionCode;
    }

    /**
     * <p>Getter for the field <code>exceptionCode</code>.</p>
     *
     * @return a byte.
     */
    public byte getExceptionCode() {
        return exceptionCode;
    }


}

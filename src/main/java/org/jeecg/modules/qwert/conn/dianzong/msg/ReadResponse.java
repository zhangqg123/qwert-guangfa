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
package org.jeecg.modules.qwert.conn.dianzong.msg;

import org.jeecg.modules.qwert.conn.dianzong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.sero.io.StreamUtils;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract ReadResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class ReadResponse extends QwertResponse {
    private byte[] data;

    ReadResponse(int slaveId) throws DianzongTransportException {
        super(slaveId);
    }

    ReadResponse(int slaveId, byte[] data) throws DianzongTransportException {
        super(slaveId);
        this.data = data;
    }

    /** {@inheritDoc} */
    @Override
    protected void readResponse(ByteQueue queue) {
    	int tmpnumber = ((((queue.peek(4))<<8) & 0xff00) | ((queue.peek(5))&0x00ff))&0x0fff;
    	int numberOfBytes = tmpnumber/2;
  //      int numberOfBytes = QwertUtils.popUnsignedByte(queue);
        if (queue.size() < numberOfBytes)
            throw new ArrayIndexOutOfBoundsException();

        data = new byte[numberOfBytes];
        queue.pop(6);
        queue.pop(data);
    }

    /** {@inheritDoc} */
    @Override
    protected void writeResponse(ByteQueue queue) {
        QwertUtils.pushByte(queue, data.length);
        queue.push(data);
    }

    /**
     * <p>Getter for the field <code>data</code>.</p>
     *
     * @return an array of {@link byte} objects.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * <p>getShortData.</p>
     *
     * @return an array of {@link short} objects.
     */
    public short[] getShortData() {
//        return convertToShorts(data);
        return convertToShorts(data);
    }

    /**
     * <p>getBooleanData.</p>
     *
     * @return an array of {@link boolean} objects.
     */
    public boolean[] getBooleanData() {
        return convertToBooleans(data);
    }

    /**
     * <p>toString.</p>
     *
     * @param numeric a boolean.
     * @return a {@link java.lang.String} object.
     */
    public String toString(boolean numeric) {
        if (data == null)
            return "ReadResponse [null]";
        return "ReadResponse [len=" + (numeric ? data.length / 2 : data.length * 8) + ", " + StreamUtils.dumpHex(data)
                + "]";
    }
}

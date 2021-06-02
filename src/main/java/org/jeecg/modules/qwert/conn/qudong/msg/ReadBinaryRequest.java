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
import org.jeecg.modules.qwert.conn.qudong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract ReadBinaryRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class ReadBinaryRequest extends QwertRequest {
    private int startOffset;
    private int numberOfBits;

    /**
     * <p>Constructor for ReadBinaryRequest.</p>
     *
     * @param slaveId a int.
     * @param startOffset a int.
     * @param numberOfBits a int.
     * @throws org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException if any.
     */
    public ReadBinaryRequest(int slaveId, int startOffset, int numberOfBits) throws QudongTransportException {
        super(slaveId);
        this.startOffset = startOffset;
        this.numberOfBits = numberOfBits;
    }

    /** {@inheritDoc} */
    @Override
    public void validate(Qwert modbus) throws QudongTransportException {
        QwertUtils.validateOffset(startOffset);
        modbus.validateNumberOfBits(numberOfBits);
        QwertUtils.validateEndOffset(startOffset + numberOfBits - 1);
    }

    ReadBinaryRequest(int slaveId) throws QudongTransportException {
        super(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    protected void writeRequest(ByteQueue queue) {
        QwertUtils.pushShort(queue, startOffset);
        QwertUtils.pushShort(queue, numberOfBits);
    }

    /** {@inheritDoc} */
    @Override
    protected void readRequest(ByteQueue queue) {
        startOffset = QwertUtils.popUnsignedShort(queue);
        numberOfBits = QwertUtils.popUnsignedShort(queue);
    }

    /**
     * <p>getData.</p>
     *
     * @param processImage a {@link ProcessImage} object.
     * @return an array of {@link byte} objects.
     * @throws org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException if any.
     */
    protected byte[] getData(ProcessImage processImage) throws QudongTransportException {
        boolean[] data = new boolean[numberOfBits];

        // Get the data from the process image.
        for (int i = 0; i < numberOfBits; i++)
            data[i] = getBinary(processImage, i + startOffset);

        // Convert the boolean array into an array of bytes.
        return convertToBytes(data);
    }

    /**
     * <p>getBinary.</p>
     *
     * @param processImage a {@link ProcessImage} object.
     * @param index a int.
     * @return a boolean.
     * @throws org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException if any.
     */
    abstract protected boolean getBinary(ProcessImage processImage, int index) throws QudongTransportException;

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReadBinaryRequest [startOffset=" + startOffset + ", numberOfBits=" + numberOfBits + "]";
    }
}

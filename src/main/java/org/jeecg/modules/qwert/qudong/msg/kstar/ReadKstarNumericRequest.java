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
package org.jeecg.modules.qwert.qudong.msg.kstar;

import org.jeecg.modules.qwert.qudong.ProcessImage;
import org.jeecg.modules.qwert.qudong.Qwert;
import org.jeecg.modules.qwert.qudong.base.QwertUtils;
import org.jeecg.modules.qwert.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.qudong.msg.QwertRequest;
import org.jeecg.modules.qwert.qudong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract ReadDianzongNumericRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class ReadKstarNumericRequest extends QwertRequest {
	private int cid1;
    private int cid2;

    /**
     * <p>Constructor for ReadDianzongNumericRequest.</p>
     *
     * @param slaveId a int.
     * @param startOffset a int.
     * @param numberOfRegisters a int.
     * @throws QudongTransportException if any.
     */
    public ReadKstarNumericRequest(int slaveId, int cid1,int cid2) throws QudongTransportException {
        super(slaveId);
        this.cid1 = cid1;
        this.cid1 = cid2;
    }

    /** {@inheritDoc} */
    @Override
    public void validate(Qwert modbus) throws QudongTransportException {
        QwertUtils.validateOffset(cid1);
    }

    public ReadKstarNumericRequest(int slaveId) throws QudongTransportException {
        super(slaveId);
    }

    @Override
    protected void writeRequest(ByteQueue queue) {
        queue.popAll();
        queue.push('Q');
        queue.push('1');
    }

    /** {@inheritDoc} */
    @Override
    protected void readRequest(ByteQueue queue) {
        cid1 = queue.popInt(queue.pop());
    }

    /**
     * <p>getData.</p>
     *
     * @param processImage a {@link ProcessImage} object.
     * @return an array of {@link byte} objects.
     * @throws QudongTransportException if any.
     */
    protected byte[] getData(ProcessImage processImage) throws QudongTransportException {
        short[] data = new short[12];

        // Get the data from the process image.
        for (int i = 0; i < 12; i++)
            data[i] = getNumeric(processImage, i );

        return convertToBytes(data);
    }

    /**
     * <p>getNumeric.</p>
     *
     * @param processImage a {@link ProcessImage} object.
     * @param index a int.
     * @return a short.
     * @throws QudongTransportException if any.
     */
    abstract protected short getNumeric(ProcessImage processImage, int index) throws QudongTransportException;

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReadKstarNumericRequest [cid1=" + cid1  + "]";
    }
}

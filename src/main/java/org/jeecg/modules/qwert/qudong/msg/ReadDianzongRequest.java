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
package org.jeecg.modules.qwert.qudong.msg;

import org.jeecg.modules.qwert.qudong.ProcessImage;
import org.jeecg.modules.qwert.qudong.code.FunctionCode;
import org.jeecg.modules.qwert.qudong.exception.QudongTransportException;

/**
 * <p>ReadDianzongRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ReadDianzongRequest extends ReadDianzongNumericRequest {
	private float ver;
    /**
     * <p>Constructor for ReadDianzongRequest.</p>
     *
     * @param slaveId a int.
     * @param startOffset a int.
     * @param numberOfRegisters a int.
     * @throws org.jeecg.modules.qwert.qudong.exception.QudongTransportException if any.
     */
    public ReadDianzongRequest(float ver,int slaveId, int cid1, int cid2,int lenid)
            throws QudongTransportException {
        super(ver,slaveId, cid1, cid2,lenid);
        this.ver=ver;
    }

    ReadDianzongRequest(int slaveId) throws QudongTransportException {
        super(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.READ_DIANZONG_REGISTERS;
    }

    @Override
    public QwertResponse handleImpl(ProcessImage processImage) throws QudongTransportException {
        return new ReadDianzongResponse(slaveId, getData(processImage));
    }

    /** {@inheritDoc} */
    @Override
    protected short getNumeric(ProcessImage processImage, int index) throws QudongTransportException {
        return processImage.getHoldingRegister(index);
    }

    @Override
    public QwertResponse getResponseInstance(int slaveId) throws QudongTransportException {
        return new ReadDianzongResponse(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReadDianzongRequest [slaveId=" + slaveId + ", getFunctionCode()=" + getFunctionCode()
                + ", toString()=" + super.toString() + "]";
    }

}

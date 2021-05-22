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

import org.jeecg.modules.qwert.conn.dianzong.Qwert;
import org.jeecg.modules.qwert.conn.dianzong.ProcessImage;
import org.jeecg.modules.qwert.conn.dianzong.code.FunctionCode;
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue;

/**
 * <p>ReportSlaveIdRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ReportSlaveIdRequest extends QwertRequest {
    /**
     * <p>Constructor for ReportSlaveIdRequest.</p>
     *
     * @param slaveId a int.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    public ReportSlaveIdRequest(int slaveId) throws DianzongTransportException {
        super(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    public void validate(Qwert modbus) {
        // no op
    }

    /** {@inheritDoc} */
    @Override
    protected void writeRequest(ByteQueue queue) {
        // no op
    }

    /** {@inheritDoc} */
    @Override
    protected void readRequest(ByteQueue queue) {
        // no op
    }

    @Override
    QwertResponse getResponseInstance(int slaveId) throws DianzongTransportException {
        return new ReportSlaveIdResponse(slaveId);
    }

    @Override
    QwertResponse handleImpl(ProcessImage processImage) throws DianzongTransportException {
        return new ReportSlaveIdResponse(slaveId, processImage.getReportSlaveIdData());
    }

    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.REPORT_SLAVE_ID;
    }

	@Override
	protected void writeRequest2(StringBuilder qs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getVer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getRetData() {
		// TODO Auto-generated method stub
		return null;
	}
}

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

import org.jeecg.modules.qwert.conn.dianzong.code.FunctionCode;
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;

/**
 * <p>ReadDianzongResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ReadDianzongResponse extends ReadResponse {
	private byte[] data;
    ReadDianzongResponse(int slaveId, byte[] data) throws DianzongTransportException {
        super(slaveId, data);
        this.data=data;
    }

    ReadDianzongResponse(int slaveId) throws DianzongTransportException {
        super(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.READ_DIANZONG_REGISTERS;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReadDianzongResponse [exceptionCode=" + exceptionCode + ", slaveId=" + slaveId
                + ", getFunctionCode()=" + getFunctionCode() + ", isException()=" + isException()
                + ", getExceptionMessage()=" + getExceptionMessage() + ", getExceptionCode()=" + getExceptionCode()
                + ", toString()=" + super.toString(true) + "]";
    }

	@Override
	public float getVer() {
		// TODO Auto-generated method stub
		return 2.1f;
	}
	
	@Override
	public byte[] getRetData() {
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	protected void writeImpl2(StringBuilder qs) {
        qs.append(60);
        qs.append(47);
        String lenid = chkLength(48);
        qs.append(lenid);
	}
	
	public static String chkLength(int value){
		byte a1 = (byte) (value & 0xf);
		byte a2 = (byte) ((value>>4) & 0xf);
		byte a3 = (byte) ((value>>8) & 0xf);
		int sum = a1+a2+a3;
		sum=((~sum%0x10000+1)& 0xf)<<12 | (value&0xffff);
		return Integer.toHexString(sum);
	}

	
}

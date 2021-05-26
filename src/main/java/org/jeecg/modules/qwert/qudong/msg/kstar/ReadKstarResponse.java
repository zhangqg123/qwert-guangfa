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

import org.jeecg.modules.qwert.qudong.base.QwertUtils;
import org.jeecg.modules.qwert.qudong.code.FunctionCode;
import org.jeecg.modules.qwert.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.qudong.msg.ReadResponse;
import org.jeecg.modules.qwert.qudong.sero.util.queue.ByteQueue;

/**
 * <p>ReadDianzongResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ReadKstarResponse extends ReadResponse {
	private byte[] data;
	private String msg;

    ReadKstarResponse(int slaveId, byte[] data) throws QudongTransportException {
        super(slaveId, data);
        this.data=data;
    }

    ReadKstarResponse(int slaveId) throws QudongTransportException {
        super(slaveId);
    }
    public ReadKstarResponse(int slaveId, String msg) throws QudongTransportException {
        super(slaveId);
        this.msg=msg;
    }
    /** {@inheritDoc} */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.READ_KSTAR_REGISTERS;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReadDianzongResponse [exceptionCode=" + exceptionCode + ", slaveId=" + slaveId
                + ", getFunctionCode()=" + getFunctionCode() + ", isException()=" + isException()
                + ", getExceptionMessage()=" + getExceptionMessage() + ", getExceptionCode()=" + getExceptionCode()
                + ", toString()=" + super.toString(true) + "]";
    }

	
	public String getMessage() {
		// TODO Auto-generated method stub
		return msg;
	}

	
    @Override
    final protected void writeImpl(ByteQueue queue) {
    	if(simulator==0) {
		}else {
            writeResponse(queue);
		}
    }
	
	public static String chkLength(int value){
		byte a1 = (byte) (value & 0xf);
		byte a2 = (byte) ((value>>4) & 0xf);
		byte a3 = (byte) ((value>>8) & 0xf);
		int sum = a1+a2+a3;
		sum=((~sum%0x10000+1)& 0xf)<<12 | (value&0xffff);
		return Integer.toHexString(sum);
	}

    /** {@inheritDoc} */
    @Override
    protected void readResponse(ByteQueue queue) {
    	if(simulator==0) {
	        data = new byte[6];
	        queue.pop(data);
        }else {
            int numberOfBytes = QwertUtils.popUnsignedByte(queue);
            if (queue.size() < numberOfBytes)
                throw new ArrayIndexOutOfBoundsException();

            data = new byte[numberOfBytes];
            queue.pop(data);
        }
    }
    
    public short[] getShortData() {
      return convertToShorts(data);
  }

}

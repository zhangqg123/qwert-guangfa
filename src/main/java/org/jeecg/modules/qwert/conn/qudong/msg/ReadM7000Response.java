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

import org.jeecg.modules.qwert.conn.qudong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.qudong.code.FunctionCode;
import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>ReadDianzongResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ReadM7000Response extends ReadResponse {
    private byte[] data;

    ReadM7000Response(int slaveId, byte[] data) throws QudongTransportException {
        super(slaveId, data);
        this.data = data;
    }

    ReadM7000Response(int slaveId) throws QudongTransportException {
        super(slaveId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getFunctionCode() {
        return FunctionCode.READ_M7000_REGISTERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ReadDianzongResponse [exceptionCode=" + exceptionCode + ", slaveId=" + slaveId
                + ", getFunctionCode()=" + getFunctionCode() + ", isException()=" + isException()
                + ", getExceptionMessage()=" + getExceptionMessage() + ", getExceptionCode()=" + getExceptionCode()
                + ", toString()=" + super.toString(true) + "]";
    }


    public byte[] getRetData() {
        // TODO Auto-generated method stub
        return data;
    }


    @Override
    final protected void writeImpl(ByteQueue queue) {
        if (simulator == 0) {
            byte[] bur = new byte[2];
            queue.pop(bur, 0, 2);
            queue.push("02");
            queue.push("00");
            queue.push(bur);
            queue.push("06");
            queue.push("00");
            queue.push("04");
            queue.push("02");
            String lenid = chkLength(48);
            byte[] tmp = lenid.toUpperCase().getBytes();
            queue.push(tmp);
            byte[] rd = getRetData();
            queue.push(rd);
//			QwertAsciiUtils.getAsciiData(queue,2);
        } else {
            writeResponse(queue);
        }
    }

    public static String chkLength(int value) {
        byte a1 = (byte) (value & 0xf);
        byte a2 = (byte) ((value >> 4) & 0xf);
        byte a3 = (byte) ((value >> 8) & 0xf);
        int sum = a1 + a2 + a3;
        sum = ((~sum % 0x10000 + 1) & 0xf) << 12 | (value & 0xffff);
        return Integer.toHexString(sum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readResponse(ByteQueue queue) {
        if (simulator == 0) {
            data = new byte[6];
            queue.pop(data);
        } else {
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

    public String getBinData() {
        return conver2HexStr(data);
    }

    public static String conver2HexStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2));
        }
        return result.toString();
    }
}
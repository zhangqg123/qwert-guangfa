/*
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
import org.jeecg.modules.qwert.conn.dianzong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.dianzong.code.ExceptionCode;
import org.jeecg.modules.qwert.conn.dianzong.code.FunctionCode;
import org.jeecg.modules.qwert.conn.dianzong.exception.IllegalDataAddressException;
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract QwertRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class QwertRequest extends QwertMessage {
    /**
     * <p>createQwertRequest.</p>
     *
     * @param queue a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertRequest} object.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    public static QwertRequest createQwertRequest(ByteQueue queue) throws DianzongTransportException {
//    	int slaveId = QwertUtils.popUnsignedByte(queue);
    	int slaveId = queue.peek(1);
//        byte functionCode = queue.pop();
        byte functionCode = FunctionCode.READ_DIANZONG_REGISTERS;

        QwertRequest request = null;
        if (functionCode == FunctionCode.READ_DIANZONG_REGISTERS)
            request = new ReadDianzongRequest(slaveId);
//        else if (functionCode == FunctionCode.READ_INPUT_REGISTERS)
//            request = new ReadInputRegistersRequest(slaveId);
        else if (functionCode == FunctionCode.REPORT_SLAVE_ID)
            request = new ReportSlaveIdRequest(slaveId);
        // else if (functionCode == FunctionCode.WRITE_MASK_REGISTER)
        // request = new WriteMaskRegisterRequest(slaveId);
        else
            request = new ExceptionRequest(slaveId, functionCode, ExceptionCode.ILLEGAL_FUNCTION);

        request.readRequest(queue);

        return request;
    }

    QwertRequest(int slaveId) throws DianzongTransportException {
        super(slaveId);
    }

    /**
     * <p>validate.</p>
     *
     * @param modbus a {@link org.jeecg.modules.qwert.conn.dianzong.Qwert} object.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    abstract public void validate(Qwert modbus) throws DianzongTransportException;

    /**
     * <p>handle.</p>
     *
     * @param processImage a {@link org.jeecg.modules.qwert.conn.dianzong.ProcessImage} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertResponse} object.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    public QwertResponse handle(ProcessImage processImage) throws DianzongTransportException {
        try {
            try {
                return handleImpl(processImage);
            }
            catch (IllegalDataAddressException e) {
                return handleException(ExceptionCode.ILLEGAL_DATA_ADDRESS);
            }
        }
        catch (Exception e) {
            return handleException(ExceptionCode.SLAVE_DEVICE_FAILURE);
        }
    }

    abstract QwertResponse handleImpl(ProcessImage processImage) throws DianzongTransportException;

    /**
     * <p>readRequest.</p>
     *
     * @param queue a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
     */
    abstract protected void readRequest(ByteQueue queue);

    QwertResponse handleException(byte exceptionCode) throws DianzongTransportException {
        QwertResponse response = getResponseInstance(slaveId);
        response.setException(exceptionCode);
        return response;
    }

    abstract QwertResponse getResponseInstance(int slaveId) throws DianzongTransportException;

    /** {@inheritDoc} */
    @Override
    final protected void writeImpl2(StringBuilder qs) {
//        queue.push(getFunctionCode());
        writeRequest2(qs);
    }
    @Override
    final protected void writeImpl(ByteQueue queue) {
//        queue.push(getFunctionCode());
        writeRequest(queue);
    }

    /**
     * <p>writeRequest.</p>
     *
     * @param queue a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
     */
    abstract protected void writeRequest(ByteQueue queue);
    abstract protected void writeRequest2(StringBuilder qs);
}

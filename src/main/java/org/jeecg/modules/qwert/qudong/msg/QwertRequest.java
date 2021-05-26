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
package org.jeecg.modules.qwert.qudong.msg;

import org.jeecg.modules.qwert.qudong.ProcessImage;
import org.jeecg.modules.qwert.qudong.Qwert;
import org.jeecg.modules.qwert.qudong.base.QwertAsciiUtils;
import org.jeecg.modules.qwert.qudong.code.ExceptionCode;
import org.jeecg.modules.qwert.qudong.code.FunctionCode;
import org.jeecg.modules.qwert.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.qudong.exception.IllegalDataAddressException;
import org.jeecg.modules.qwert.qudong.msg.delta.ReadDeltaRequest;
import org.jeecg.modules.qwert.qudong.msg.kstar.ReadKstarRequest;
import org.jeecg.modules.qwert.qudong.sero.util.queue.ByteQueue;

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
     * @param queue a {@link ByteQueue} object.
     * @return a {@link QwertRequest} object.
     * @throws QudongTransportException if any.
     */
    public static QwertRequest createQwertRequest(ByteQueue queue) throws QudongTransportException {
        byte code = 0;
        int end=queue.size();
        if(end>0){
            code = queue.peek(0);
            end=queue.peek(end-1);
        }
    //    ByteQueue msgQueue = QwertAsciiUtils.getUnDianzongMessage(queue);
    //	int slaveId = msgQueue.peek(1);
        ByteQueue msgQueue=null;
        int slaveId=1;
        byte  functionCode = FunctionCode.READ_DIANZONG_REGISTERS;
        if(code==QwertAsciiUtils.START){
            if(end==13)
                functionCode = FunctionCode.READ_DIANZONG_REGISTERS;
            else
                functionCode = FunctionCode.READ_DELTA_REGISTERS;
        }
        if(code==QwertAsciiUtils.M7000_START){
            functionCode = FunctionCode.READ_M7000_REGISTERS;
        }
        if(code==QwertAsciiUtils.KSTAR_START){
            functionCode = FunctionCode.READ_KSTAR_REGISTERS;
        }
        QwertRequest request = null;
        if (functionCode == FunctionCode.READ_DIANZONG_REGISTERS) {
            msgQueue = QwertAsciiUtils.getUnDianzongMessage(queue);
            slaveId = msgQueue.peek(1);
            request = new ReadDianzongRequest(slaveId);
        }else if (functionCode == FunctionCode.READ_M7000_REGISTERS) {
            msgQueue = QwertAsciiUtils.getUnM7000AsciiMessage(queue);
            slaveId = msgQueue.peek(1);
            request = new ReadM7000Request(slaveId);
        }else if (functionCode == FunctionCode.READ_KSTAR_REGISTERS) {
            msgQueue = QwertAsciiUtils.getUnKstarAsciiMessage(queue);
            slaveId = 1;
            request = new ReadKstarRequest(slaveId);
        }else if (functionCode == FunctionCode.READ_DELTA_REGISTERS) {
            msgQueue = QwertAsciiUtils.getUnDeltaAsciiMessage(queue);
            slaveId = 1;
            request = new ReadDeltaRequest(slaveId);
        }else if (functionCode == FunctionCode.REPORT_SLAVE_ID)
            request = new ReportSlaveIdRequest(slaveId);
        // else if (functionCode == FunctionCode.WRITE_MASK_REGISTER)
        // request = new WriteMaskRegisterRequest(slaveId);
        else
            request = new ExceptionRequest(slaveId, functionCode, ExceptionCode.ILLEGAL_FUNCTION);

        request.readRequest(msgQueue);

        return request;
    }

    public QwertRequest(int slaveId) throws QudongTransportException {
        super(slaveId);
    }

    /**
     * <p>validate.</p>
     *
     * @param modbus a {@link Qwert} object.
     * @throws QudongTransportException if any.
     */
    abstract public void validate(Qwert modbus) throws QudongTransportException;

    /**
     * <p>handle.</p>
     *
     * @param processImage a {@link ProcessImage} object.
     * @return a {@link QwertResponse} object.
     * @throws QudongTransportException if any.
     */
    public QwertResponse handle(ProcessImage processImage) throws QudongTransportException {
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

    public abstract QwertResponse handleImpl(ProcessImage processImage) throws QudongTransportException;

    /**
     * <p>readRequest.</p>
     *
     * @param queue a {@link ByteQueue} object.
     */
    abstract protected void readRequest(ByteQueue queue);

    QwertResponse handleException(byte exceptionCode) throws QudongTransportException {
        QwertResponse response = getResponseInstance(slaveId);
        response.setException(exceptionCode);
        return response;
    }

    public abstract QwertResponse getResponseInstance(int slaveId) throws QudongTransportException;

    @Override
    final protected void writeImpl(ByteQueue queue) {
//        queue.push(getFunctionCode());
        writeRequest(queue);
    }

    /**
     * <p>writeRequest.</p>
     *
     * @param queue a {@link ByteQueue} object.
     */
    abstract protected void writeRequest(ByteQueue queue);
}

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
package org.jeecg.modules.qwert.conn.qudong.msg;

import org.jeecg.modules.qwert.conn.qudong.base.QwertAsciiUtils;
import org.jeecg.modules.qwert.conn.qudong.code.ExceptionCode;
import org.jeecg.modules.qwert.conn.qudong.code.FunctionCode;
import org.jeecg.modules.qwert.conn.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.conn.qudong.exception.IllegalFunctionException;
import org.jeecg.modules.qwert.conn.qudong.exception.SlaveIdNotEqual;
import org.jeecg.modules.qwert.conn.qudong.msg.delta.ReadDeltaResponse;
import org.jeecg.modules.qwert.conn.qudong.msg.kstar.ReadKstarResponse;
import org.jeecg.modules.qwert.conn.qudong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract QwertResponse class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class QwertResponse extends QwertMessage {
    /** Constant <code>MAX_FUNCTION_CODE=(byte) 0x80</code> */
    protected static final byte MAX_FUNCTION_CODE = (byte) 0x80;

    /**
     * <p>createQwertResponse.</p>
     *
     * @param queue a {@link ByteQueue} object.
     * @return a {@link QwertResponse} object.
     * @throws QudongTransportException if any.
     */
    public static QwertResponse createQwertResponse(ByteQueue queue) throws QudongTransportException {
        byte code = 0;
        byte functionCode = FunctionCode.READ_DIANZONG_REGISTERS;
        int end=queue.size();
        if(end>0){
            code = queue.peek(0);
            end=queue.peek(end-1);
        }

        int slaveId=1;
        String msg=null;
        if (code == QwertAsciiUtils.START) {
            if(end==13){
                functionCode = FunctionCode.READ_DIANZONG_REGISTERS;
                queue=QwertAsciiUtils.getUnDianzongMessage(queue);
                slaveId = queue.peek(1);
            }else{
                functionCode = FunctionCode.READ_DELTA_REGISTERS;
                msg=QwertAsciiUtils.getDeltaReturnMessage(queue);
                slaveId=1;
            }
        }else if (code == QwertAsciiUtils.M7000_RETURN_START) {
        	functionCode = FunctionCode.READ_M7000_REGISTERS;
        	queue=QwertAsciiUtils.getM7000ReturnMessage(queue);
            slaveId = 1;
        }else if (code == QwertAsciiUtils.KSTAR_RETURN_START) {
            functionCode = FunctionCode.READ_KSTAR_REGISTERS;
            msg=QwertAsciiUtils.getKstarReturnMessage(queue);
            slaveId = 1;
        }else{
            functionCode = FunctionCode.READ_DIANZONG_REGISTERS;
            queue=QwertAsciiUtils.getUnDianzongMessage(new ByteQueue());
            slaveId = queue.peek(1);
        }

//    	queue=QwertAsciiUtils.getUnDianzongMessage(queue);
//    	int slaveId = queue.peek(1);
//        int slaveId=1;
        boolean isException = false;

        if (greaterThan(functionCode, MAX_FUNCTION_CODE)) {
            isException = true;
            functionCode -= MAX_FUNCTION_CODE;
        }

        QwertResponse response = null;
        if (functionCode == FunctionCode.READ_DIANZONG_REGISTERS) {
//            queue = QwertAsciiUtils.getUnDianzongMessage(queue);
//            slaveId = queue.peek(1);
            response = new ReadDianzongResponse(slaveId);
        }else if (functionCode == FunctionCode.READ_M7000_REGISTERS)
            response = new ReadM7000Response(slaveId);
        else if (functionCode == FunctionCode.READ_KSTAR_REGISTERS) {
            response = new ReadKstarResponse(slaveId, msg);
            return response;
        }else if (functionCode == FunctionCode.READ_DELTA_REGISTERS) {
            response = new ReadDeltaResponse(slaveId, msg);
            return response;
        }else if (functionCode == FunctionCode.REPORT_SLAVE_ID)
            response = new ReportSlaveIdResponse(slaveId);
        else
            throw new IllegalFunctionException(functionCode, slaveId);

        response.read(queue, isException);

        return response;
    }

    protected byte exceptionCode = -1;

    QwertResponse(int slaveId) throws QudongTransportException {
        super(slaveId);
    }

    /**
     * <p>isException.</p>
     *
     * @return a boolean.
     */
    public boolean isException() {
        return exceptionCode != -1;
    }

    /**
     * <p>getExceptionMessage.</p>
     *
     * @return a {@link String} object.
     */
    public String getExceptionMessage() {
        return ExceptionCode.getExceptionMessage(exceptionCode);
    }

    void setException(byte exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    /**
     * <p>Getter for the field <code>exceptionCode</code>.</p>
     *
     * @return a byte.
     */
    public byte getExceptionCode() {
        return exceptionCode;
    }

    /** {@inheritDoc} */
    @Override
    protected void writeImpl(ByteQueue queue) {
        if (isException()) {
            queue.push((byte) (getFunctionCode() + MAX_FUNCTION_CODE));
            queue.push(exceptionCode);
        }
        else {
            queue.push(getFunctionCode());
            writeResponse(queue);
        }
    }

    /**
     * <p>writeResponse.</p>
     *
     * @param queue a {@link ByteQueue} object.
     */
    abstract protected void writeResponse(ByteQueue queue);

    void read(ByteQueue queue, boolean isException) {
        if (isException)
            exceptionCode = queue.pop();
        else
            readResponse(queue);
    }

    /**
     * <p>readResponse.</p>
     *
     * @param queue a {@link ByteQueue} object.
     */
    abstract protected void readResponse(ByteQueue queue);

    private static boolean greaterThan(byte b1, byte b2) {
        int i1 = b1 & 0xff;
        int i2 = b2 & 0xff;
        return i1 > i2;
    }

    /**
     * Ensure that the Response slave id is equal to the requested slave id
     * @param request
     * @throws QudongTransportException
     */
    public void  validateResponse(QwertRequest request) throws QudongTransportException {
        if(getSlaveId() != request.slaveId)
        	throw new SlaveIdNotEqual(request.slaveId, getSlaveId());         	
    }
    
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     * @throws Exception if any.
     */
    public static void main(String[] args) throws Exception {
        ByteQueue queue = new ByteQueue(new byte[] { 3, 2 });
        QwertResponse r = createQwertResponse(queue);
        System.out.println(r);
    }
}

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

import org.jeecg.modules.qwert.conn.dianzong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.dianzong.code.ExceptionCode;
import org.jeecg.modules.qwert.conn.dianzong.code.FunctionCode;
import org.jeecg.modules.qwert.conn.dianzong.exception.IllegalFunctionException;
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.exception.SlaveIdNotEqual;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue;

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
     * @param queue a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertResponse} object.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    public static QwertResponse createQwertResponse(ByteQueue queue) throws DianzongTransportException {
       	int slaveId = queue.peek(1);
//      byte functionCode = queue.pop();
        byte functionCode = FunctionCode.READ_DIANZONG_REGISTERS;
        boolean isException = false;

        if (greaterThan(functionCode, MAX_FUNCTION_CODE)) {
            isException = true;
            functionCode -= MAX_FUNCTION_CODE;
        }

        QwertResponse response = null;
        if (functionCode == FunctionCode.READ_DIANZONG_REGISTERS)
            response = new ReadDianzongResponse(slaveId);
//        else if (functionCode == FunctionCode.READ_INPUT_REGISTERS)
//            response = new ReadInputRegistersResponse(slaveId);
        else if (functionCode == FunctionCode.REPORT_SLAVE_ID)
            response = new ReportSlaveIdResponse(slaveId);
        else
            throw new IllegalFunctionException(functionCode, slaveId);

        response.read(queue, isException);

        return response;
    }

    protected byte exceptionCode = -1;

    QwertResponse(int slaveId) throws DianzongTransportException {
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
     * @return a {@link java.lang.String} object.
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
    final protected void writeImpl(ByteQueue queue) {
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
     * @param queue a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
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
     * @param queue a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
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
     * @throws DianzongTransportException
     */
    public void  validateResponse(QwertRequest request) throws DianzongTransportException {
        if(getSlaveId() != request.slaveId)
        	throw new SlaveIdNotEqual(request.slaveId, getSlaveId());         	
    }
    
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {
        ByteQueue queue = new ByteQueue(new byte[] { 3, 2 });
        QwertResponse r = createQwertResponse(queue);
        System.out.println(r);
    }
}

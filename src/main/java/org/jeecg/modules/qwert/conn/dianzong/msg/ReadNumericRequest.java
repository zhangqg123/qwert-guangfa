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
import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract ReadNumericRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class ReadNumericRequest extends QwertRequest {
    private float ver;
	private int cid1;
    private int cid2;
    private int lenid;

    /**
     * <p>Constructor for ReadNumericRequest.</p>
     *
     * @param slaveId a int.
     * @param startOffset a int.
     * @param numberOfRegisters a int.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    public ReadNumericRequest(float ver,int slaveId, int cid1, int cid2,int lenid) throws DianzongTransportException {
        super(slaveId);
        this.ver=ver;
        this.cid1 = cid1;
        this.cid2 = cid2;
        this.lenid=lenid;
    }

    /** {@inheritDoc} */
    @Override
    public void validate(Qwert modbus) throws DianzongTransportException {
        QwertUtils.validateOffset(cid1);
        modbus.validateNumberOfRegisters(cid2);
        QwertUtils.validateEndOffset(cid1 + cid2 - 1);
    }

    ReadNumericRequest(int slaveId) throws DianzongTransportException {
        super(slaveId);
    }

    /** {@inheritDoc} */
    @Override
    protected void writeRequest2(StringBuilder qs) {
 //       QwertUtils.pushByte(queue, cid1);
 //       QwertUtils.pushByte(queue, cid2);
        qs.append(cid1);
        qs.append(cid2);
        if(lenid==0) {
   //     	QwertUtils.pushShort(queue, lenid);
   //     	QwertUtils.pushShort(queue, lenid);
        	qs.append("0000");
        }
    }
    @Override
    protected void writeRequest(ByteQueue queue) {
/*        QwertUtils.pushByte(queue, cid1);
        QwertUtils.pushByte(queue, cid2);
        if(lenid==0) {
            QwertUtils.pushShort(queue, 0);        	
            QwertUtils.pushShort(queue, 0);        	
        }
*/
    	queue.push(""+cid1);
        queue.push(""+cid2);
        queue.push("00000000");
    }

    /** {@inheritDoc} */
    @Override
    protected void readRequest(ByteQueue queue) {
    	String tmpver = Integer.toHexString(queue.pop() & 0xFF);
    	ver=Float.parseFloat(tmpver.substring(0,1)+"."+tmpver.substring(1));
    	queue.pop();
        cid1 = queue.popInt(queue.pop());
        cid2 = queue.popInt(queue.pop());
        lenid = queue.popInt(queue.pop());
    }

    /**
     * <p>getData.</p>
     *
     * @param processImage a {@link org.jeecg.modules.qwert.conn.dianzong.ProcessImage} object.
     * @return an array of {@link byte} objects.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    protected byte[] getData(ProcessImage processImage) throws DianzongTransportException {
        short[] data = new short[12];

        // Get the data from the process image.
        for (int i = 0; i < 12; i++)
            data[i] = getNumeric(processImage, i );

        return convertToBytes(data);
    }

    /**
     * <p>getNumeric.</p>
     *
     * @param processImage a {@link org.jeecg.modules.qwert.conn.dianzong.ProcessImage} object.
     * @param index a int.
     * @return a short.
     * @throws org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException if any.
     */
    abstract protected short getNumeric(ProcessImage processImage, int index) throws DianzongTransportException;

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReadNumericRequest [ver=" + ver + ",cid1=" + cid1 + ", cid2=" + cid2 + ", lenid=" + lenid + "]";
    }
}

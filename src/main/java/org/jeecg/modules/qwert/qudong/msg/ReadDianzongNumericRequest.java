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
import org.jeecg.modules.qwert.qudong.base.QwertUtils;
import org.jeecg.modules.qwert.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.qudong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract ReadDianzongNumericRequest class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class ReadDianzongNumericRequest extends QwertRequest {
    private float ver;
	private int cid1;
    private int cid2;
    private int lenid;

    /**
     * <p>Constructor for ReadDianzongNumericRequest.</p>
     *
     * @param slaveId a int.
     * @param startOffset a int.
     * @param numberOfRegisters a int.
     * @throws org.jeecg.modules.qwert.qudong.exception.QudongTransportException if any.
     */
    public ReadDianzongNumericRequest(float ver,int slaveId, int cid1, int cid2,int lenid) throws QudongTransportException {
        super(slaveId);
        this.ver=ver;
        this.cid1 = cid1;
        this.cid2 = cid2;
        this.lenid=lenid;
    }

    /** {@inheritDoc} */
    @Override
    public void validate(Qwert modbus) throws QudongTransportException {
        QwertUtils.validateOffset(cid1);
        modbus.validateNumberOfRegisters(cid2);
        QwertUtils.validateEndOffset(cid1 + cid2 - 1);
    }

    ReadDianzongNumericRequest(int slaveId) throws QudongTransportException {
        super(slaveId);
    }

    @Override
    protected void writeRequest(ByteQueue queue) {
    	byte[] bur = new byte[2];
		queue.pop(bur,0,2);
        StringBuilder qs=new StringBuilder();
    	String[] tmp = (""+this.ver).split("\\.");

        QwertUtils.pushByte(queue, Integer.valueOf(tmp[0]));
        QwertUtils.pushByte(queue, Integer.valueOf(tmp[1]));
        queue.push(bur);
    	String tmp1 = "0"+(""+cid1).substring(0,1);
    	String tmp2 = "0"+(""+cid1).substring(1,2);
    	if(cid1>=10) {
    		queue.push(tmp1);
   			queue.push(tmp2);
    	}else {
    		queue.push(tmp1);
    	}
    	String tmp3 = "0"+(""+cid2).substring(0,1);
    	String tmp4 = "0"+(""+cid2).substring(1,2);
    	if(cid2>=10) {
    		queue.push(tmp3);
   			queue.push(tmp4);
    	}else {
    		queue.push(tmp3);
    	}
        queue.push("00");
        queue.push("00");
        queue.push("00");
        queue.push("00");
//        QwertAsciiUtils.getAsciiData(queue,1);
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
     * @param processImage a {@link ProcessImage} object.
     * @return an array of {@link byte} objects.
     * @throws org.jeecg.modules.qwert.qudong.exception.QudongTransportException if any.
     */
    protected byte[] getData(ProcessImage processImage) throws QudongTransportException {
        short[] data = new short[12];

        // Get the data from the process image.
        for (int i = 0; i < 12; i++)
            data[i] = getNumeric(processImage, i );

        return convertToBytes(data);
    }

    /**
     * <p>getNumeric.</p>
     *
     * @param processImage a {@link ProcessImage} object.
     * @param index a int.
     * @return a short.
     * @throws org.jeecg.modules.qwert.qudong.exception.QudongTransportException if any.
     */
    abstract protected short getNumeric(ProcessImage processImage, int index) throws QudongTransportException;

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "ReadDianzongNumericRequest [ver=" + ver + ",cid1=" + cid1 + ", cid2=" + cid2 + ", lenid=" + lenid + "]";
    }
}

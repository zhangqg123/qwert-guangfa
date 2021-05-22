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
package org.jeecg.modules.qwert.conn.dianzong.serial.dianzong;

import org.jeecg.modules.qwert.conn.dianzong.exception.DianzongTransportException;
import org.jeecg.modules.qwert.conn.dianzong.msg.QwertMessage;
import org.jeecg.modules.qwert.conn.dianzong.serial.SerialMessage;
import org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract DianzongMessage class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class DianzongMessage extends SerialMessage {
//    private static final byte START = ':';
//    private static final byte[] END = { '\r', '\n' };
    private static final byte START = '~';
    private static final byte[] END = { '\r', '\n' };

    DianzongMessage(QwertMessage QwertMessage) {
        super(QwertMessage);
    }

    /**
     * <p>getUnDianzongMessage.</p>
     *
     * @param queue a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
     * @throws DianzongTransportException 
     */
    protected static ByteQueue getUnDianzongMessage(ByteQueue queue) throws DianzongTransportException {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != START)
            throw new DianzongTransportException("Invalid message start: " + b);

        // Find the end indicator
        int end = queue.indexOf(END);
        if (end == -1)
            throw new ArrayIndexOutOfBoundsException();

        // Remove the message from the queue, leaving the LRC there
        byte[] asciiBytes = new byte[end - 4];
        queue.pop(asciiBytes);
        ByteQueue msgQueue = new ByteQueue(asciiBytes);

        // Pop off the LRC
        short givenLrc = readAscii2(queue);

        // Pop the end indicator off of the queue
        queue.pop(END.length);


        // Check the LRC
        int calcLrc = calculateDRC(msgQueue);
        if (calcLrc != givenLrc)
            throw new DianzongTransportException("LRC mismatch: given=" + (givenLrc & 0xff) + ", calc="
                    + (calcLrc & 0xff));
        // Convert to unascii
        fromAscii(msgQueue, msgQueue.size());

        return msgQueue;
    }

    /**
     * <p>getAsciiData.</p>
     *
     * @param queue a {@link org.jeecg.modules.qwert.conn.dianzong.sero.util.queue.ByteQueue} object.
     * @return an array of {@link byte} objects.
     */
    protected byte[] getAsciiData(ByteQueue queue,int r) {
        int unasciiLen = queue.size();
        int drc = 0;
        if(r==2) {
        	byte[] bur = new byte[12];
        	byte[] bur2=null;
			queue.pop(bur,0,12);
            toAscii(queue, queue.size());
            bur2=queue.popAll();
        	queue.push(bur);
        	queue.push(bur2);
	        drc = calculateDRC(queue);
//	        unasciiLen = queue.size();
	        bur2=queue.popAll();
	        queue.push(START);
	        queue.push(bur2);
//	        toAscii(queue, unasciiLen);
        }
		if(r==1) {
			unasciiLen = queue.size();
			drc = calculateDRC(queue);
	
	        // Convert the message to ascii
	        queue.push(START);
	        toAscii2(queue, unasciiLen);
        }
        String tmp = Integer.toHexString(drc);
        writeAscii2(queue, drc);
        queue.push(END);

        // Return the data.
        return queue.popAll();
    }

    /**
     * <p>getMessageData.</p>
     *
     * @return an array of {@link byte} objects.
     * @throws Exception 
     */
    public byte[] getMessageData2(int r) {
        ByteQueue queue = new ByteQueue();
   //     StringBuilder qs=new StringBuilder();
        QwertMessage.write2(queue,r);
 /*       ByteQueue queue = null;
		try {
			queue = new ByteQueue(toBytes(qs.toString().toUpperCase()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
        return getAsciiData(queue,r);
    }
    
    public byte[] getMessageData() {
        ByteQueue queue = new ByteQueue();
        QwertMessage.write(queue);
        return getAsciiData(queue,0);
    }
    
    public byte[] toBytes(String s) throws Exception {
        return s.getBytes("ASCII");
    }

    private static byte calculateLRC(ByteQueue queue, int start, int len) {
        int lrc = 0;
        for (int i = 0; i < len; i++)
            lrc -= queue.peek(i + start);
        return (byte) (lrc & 0xff);
    }
    
	public static int calculateDRC(ByteQueue queue) {
 		int plus = 0;
		int remainder = 0;
		
		for (int i = 0; i < queue.size(); i++) {
			plus += queue.peek(i);
		}
		//求和的值模65536后余
		remainder = plus % 65536;
		int a1 = (~remainder+1);
		return a1;
	}

    private static void toAscii2(ByteQueue queue, int unasciiLen) {
        for (int i = 0; i < unasciiLen; i++)
            writeAscii2(queue, queue.pop());
    }
    private static void toAscii(ByteQueue queue, int unasciiLen) {
        for (int i = 0; i < unasciiLen; i++)
            writeAscii(queue, queue.pop());
    }

    private static void writeAscii2(ByteQueue to, byte b) {
 //       to.push(lookupAscii[b & 0xf0]);
        to.push(lookupAscii[b & 0x0f]);
    }
    private static void writeAscii(ByteQueue to, byte b) {
        to.push(lookupAscii[b & 0xf0]);
        to.push(lookupAscii[b & 0x0f]);
    }
    
    private static void writeAscii2(ByteQueue to, int b) {
        to.push(lookupAscii[b>>8 & 0xf0]);
        to.push(lookupAscii[b>>8 & 0x0f]);
        to.push(lookupAscii[b & 0xf0]);
        to.push(lookupAscii[b & 0x0f]);
    }

    private static void fromAscii(ByteQueue queue, int asciiLen) {
        int len = asciiLen / 2;
        for (int i = 0; i < len; i++)
            queue.push(readAscii(queue));
    }

    private static byte readAscii(ByteQueue from) {
        return (byte) ((lookupUnascii[from.pop()] << 4) | lookupUnascii[from.pop()]);
    }
    
    private static short readAscii2(ByteQueue from) {
    	short aa = (short) ((lookupUnascii[from.pop()] << 12) | (lookupUnascii[from.pop()] << 8) | (lookupUnascii[from.pop()] << 4) | lookupUnascii[from.pop()]);
        return aa;
    }

    private static byte[] lookupAscii = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x41, 0x42, 0x43,
            0x44, 0x45, 0x46, 0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x32, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x33,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x34, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x35, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x37, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x38, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x39, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x41, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x42, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x43,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x44, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x45, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x46, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };

    private static byte[] lookupUnascii = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a,
            0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, };
}

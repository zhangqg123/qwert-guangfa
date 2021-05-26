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
package org.jeecg.modules.qwert.qudong.base;

import org.jeecg.modules.qwert.qudong.exception.QudongTransportException;
import org.jeecg.modules.qwert.qudong.sero.util.queue.ByteQueue;

/**
 * <p>Abstract DianzongMessage class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class QwertAsciiUtils{
//    private static final byte START = ':';
//    private static final byte[] END = { '\r', '\n' };
    public static final byte START = '~';
    public static final byte M7000_START = '$';
    public static final byte KSTAR_START = 'Q';
    public static final byte KSTAR_RETURN_START = '(';
    public static final byte M7000_RETURN_START = '!';
    private static final byte[] END = { '\r' };

    public static ByteQueue getUnDianzongMessage(ByteQueue queue) throws QudongTransportException {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != START)
            throw new QudongTransportException("Invalid message start: " + b);

        // Find the end indicator
        int end = queue.indexOf(END);
        if (end == -1)
            throw new ArrayIndexOutOfBoundsException();

        // Remove the message from the queue, leaving the LRC there
        byte[] asciiBytes = new byte[end - 4];
        queue.pop(asciiBytes);
        ByteQueue msgQueue = new ByteQueue(asciiBytes);

        // Pop off the LRC
        short givenLrc = QwertAsciiUtils.readAscii2(queue);

        // Pop the end indicator off of the queue
        queue.pop(END.length);


        // Check the LRC
        int calcLrc = QwertAsciiUtils.calculateDRC(msgQueue);
        if (calcLrc != givenLrc)
            throw new QudongTransportException("LRC mismatch: given=" + (givenLrc & 0xff) + ", calc="
                    + (calcLrc & 0xff));
        // Convert to unascii
        QwertAsciiUtils.fromAscii(msgQueue, msgQueue.size());

        return msgQueue;
    }
    public static ByteQueue getUnM7000AsciiMessage(ByteQueue queue) throws QudongTransportException {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != M7000_START)
            throw new QudongTransportException("Invalid message start: " + b);

        // Find the end indicator
        int end = queue.indexOf(END);
        if (end == -1)
            throw new ArrayIndexOutOfBoundsException();

        // Remove the message from the queue, leaving the LRC there
        byte[] asciiBytes = new byte[end];
        queue.pop(asciiBytes);
        ByteQueue msgQueue = new ByteQueue(asciiBytes);

        // Pop the end indicator off of the queue
        queue.pop(END.length);
        // Convert to unascii
        QwertAsciiUtils.fromAsciiM7000(msgQueue, msgQueue.size());

        return msgQueue;
    }

    public static ByteQueue getUnKstarAsciiMessage(ByteQueue queue) throws QudongTransportException {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != KSTAR_START)
            throw new QudongTransportException("Invalid message start: " + b);

        // Find the end indicator
        int end = queue.indexOf(END);
        if (end == -1)
            throw new ArrayIndexOutOfBoundsException();

        // Remove the message from the queue, leaving the LRC there
        byte[] asciiBytes = new byte[end];
        queue.pop(asciiBytes);
        ByteQueue msgQueue = new ByteQueue(asciiBytes);

        // Pop the end indicator off of the queue
        queue.pop(END.length);
        // Convert to unascii
        QwertAsciiUtils.fromAsciiM7000(msgQueue, msgQueue.size());

        return msgQueue;
    }
    public static ByteQueue getUnDeltaAsciiMessage(ByteQueue queue) throws QudongTransportException {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != START)
            throw new QudongTransportException("Invalid message start: " + b);

        // Find the end indicator
//        int end = queue.indexOf(END);
//        if (end == -1)
//            throw new ArrayIndexOutOfBoundsException();

        // Remove the message from the queue, leaving the LRC there
        byte[] asciiBytes = new byte[queue.size()];
        queue.pop(asciiBytes);
        ByteQueue msgQueue = new ByteQueue(asciiBytes);

        // Pop the end indicator off of the queue
//        queue.pop(END.length);
        // Convert to unascii
//        QwertAsciiUtils.fromAsciiDelta(msgQueue, msgQueue.size());

        return msgQueue;
    }

    public static ByteQueue getM7000ReturnMessage(ByteQueue queue) throws QudongTransportException {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != M7000_RETURN_START)
            throw new QudongTransportException("Invalid message start: " + b);

        // Find the end indicator
        int end = queue.indexOf(END);
        if (end == -1)
            throw new ArrayIndexOutOfBoundsException();

        // Remove the message from the queue, leaving the LRC there
        byte[] asciiBytes = new byte[end];
        queue.pop(asciiBytes);
        ByteQueue msgQueue = new ByteQueue(asciiBytes);

        // Pop the end indicator off of the queue
        queue.pop(END.length);
        // Convert to unascii
        QwertAsciiUtils.fromAsciiM7000(msgQueue, msgQueue.size());

        return msgQueue;
    }

    public static String getKstarReturnMessage(ByteQueue queue) throws QudongTransportException {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != KSTAR_RETURN_START)
            throw new QudongTransportException("Invalid message start: " + b);

        // Find the end indicator
        int end = queue.indexOf(END);
        if (end == -1)
            throw new ArrayIndexOutOfBoundsException();

        // Remove the message from the queue, leaving the LRC there
        byte[] asciiBytes = new byte[end];
        queue.pop(asciiBytes);
        ByteQueue msgQueue = new ByteQueue(asciiBytes);

        // Pop the end indicator off of the queue
        queue.pop(END.length);
        // Convert to unascii
        String msg=QwertAsciiUtils.fromAsciiKstar(msgQueue, msgQueue.size());

        return msg;
    }
    public static String getDeltaReturnMessage(ByteQueue queue) throws QudongTransportException {
        // Validate that the message starts with the required indicator
        byte b = queue.pop();
        if (b != START)
            throw new QudongTransportException("Invalid message start: " + b);
        byte[] bur = new byte[6];
        queue.pop(bur,0,6);
        ByteQueue burQueue = new ByteQueue(3);
        burQueue.push(bur[3]);
        burQueue.push(bur[4]);
        burQueue.push(bur[5]);
        String len = fromAsciiKstar(burQueue, burQueue.size());
        int aa = Integer.parseInt(len);
        byte[] msgQueue = new byte[aa];
        queue.pop(msgQueue,0,aa);
        queue.pop(queue.size());
        queue.push(msgQueue);
        // Convert to unascii
        String msg=QwertAsciiUtils.fromAsciiKstar(queue, queue.size());

        return msg;
    }

    /**
     * <p>getAsciiData.</p>
     *
     * @param queue a {@link org.jeecg.modules.qwert.qudong.sero.util.queue.ByteQueue} object.
     * @return an array of {@link byte} objects.
     */
    public static byte[] getAsciiData(ByteQueue queue,int r) {
        int unasciiLen = queue.size();
        int drc = 0;
        if(r==2) {
        	byte[] bur = new byte[12];
        	byte[] bur2=null;
			queue.pop(bur,0,12);
            toAscii(queue, queue.size());
            bur2=queue.popAll();
			queue=new ByteQueue(bur);
			toAscii2(queue,queue.size());
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
	        toAscii2(queue, unasciiLen);
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
//        return null;
    }

    public static byte[] get7000AsciiData(ByteQueue queue,int r) {
        int unasciiLen = queue.size();
        if(r==1) {
            // Convert the message to ascii
            queue.push(M7000_START);
            toAscii2(queue, unasciiLen);
        }
        queue.push(END);

        // Return the data.
        return queue.popAll();
//        return null;
    }
    public static byte[] getKstarAsciiData(ByteQueue queue,int r) {
        int unasciiLen = queue.size();
        if(r==1) {
        }
        queue.push(END);
        // Return the data.
        return queue.popAll();
    }

    public static byte[] getDeltaAsciiData(ByteQueue queue,int r) {
//        queue.push(END);
        return queue.popAll();
    }

    public static byte[] toBytes(String s) throws Exception {
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

    public static void toAscii2(ByteQueue queue, int unasciiLen) {
        for (int i = 0; i < unasciiLen; i++)
            writeAscii2(queue, queue.pop());
    }
    public static void toAscii(ByteQueue queue, int unasciiLen) {
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
    
    public static void writeAscii2(ByteQueue to, int b) {
        to.push(lookupAscii[b>>8 & 0xf0]);
        to.push(lookupAscii[b>>8 & 0x0f]);
        to.push(lookupAscii[b & 0xf0]);
        to.push(lookupAscii[b & 0x0f]);
    }

    public static void fromAscii(ByteQueue queue, int asciiLen) {
        int len = asciiLen / 2;
        for (int i = 0; i < len; i++)
            queue.push(readAscii(queue));
    }
    public static void fromAsciiM7000(ByteQueue queue, int asciiLen) {
        int len = asciiLen ;
        for (int i = 0; i < len; i++)
            queue.push(readAsciiM7000(queue));
    }
    public static void fromAsciiDelta(ByteQueue queue, int asciiLen) {
        int len = asciiLen ;
        for (int i = 0; i < len; i++)
            queue.push(readAsciiM7000(queue));
    }
    public static String fromAsciiKstar(ByteQueue queue, int asciiLen) {
        int len = asciiLen ;
        StringBuilder sb= new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(readAsciiKstar(queue));
        return sb.toString();
    }

    private static byte readAsciiM7000(ByteQueue from) {
//        int a1 = (lookupUnascii[from.pop()] << 4);
        byte a2 = lookupUnascii[from.pop()];
        return a2;
//        return (byte) ((lookupUnascii[from.pop()] << 4) | lookupUnascii[from.pop()]);
    }
    private static String readAsciiKstar(ByteQueue from) {
//        int a1 = (lookupUnascii[from.pop()] << 4);
        byte a1 = from.pop();
        if(a1==46){
            return ".";
        }
        if(a1==32){
            return " ";
        }
        if(a1==59){
            return ";";
        }
        byte a2 = lookupUnascii[a1];
        String a3 = "" + a2;
        return a3;
    }
    private static byte readAscii(ByteQueue from) {
        return (byte) ((lookupUnascii[from.pop()] << 4) | lookupUnascii[from.pop()]);
    }
    
    public static short readAscii2(ByteQueue from) {
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

package org.jeecg.modules.qwert.conn.dianzon.test;

import java.util.Random;

import org.jeecg.modules.qwert.conn.dianzong.BasicProcessImage;
import org.jeecg.modules.qwert.conn.dianzong.QwertFactory;
import org.jeecg.modules.qwert.conn.dianzong.QwertSlaveSet;
import org.jeecg.modules.qwert.conn.dianzong.ProcessImage;
import org.jeecg.modules.qwert.conn.dianzong.ProcessImageListener;
import org.jeecg.modules.qwert.conn.dianzong.code.DataType;
import org.jeecg.modules.qwert.conn.dianzong.code.RegisterRange;
import org.jeecg.modules.qwert.conn.dianzong.exception.IllegalDataAddressException;
import org.jeecg.modules.qwert.conn.dianzong.exception.ModbusInitException;
import org.jeecg.modules.qwert.conn.dianzong.serial.QwertSerialPortWrapper;

public class ListenerTest {
    static Random random = new Random();
    static float ir1Value = -100;

    public static void main(String[] args) throws Exception {
    	String commPortId = "COM2";
    	int baudRate = 9600;
    	int flowControlIn = 0;
		int flowControlOut = 0; 
		int dataBits = 8;
		int stopBits = 1;
		int parity = 0;
    	QwertSerialPortWrapper wrapper = new QwertSerialPortWrapper(commPortId, baudRate, flowControlIn, flowControlOut, dataBits, stopBits, parity);
        QwertFactory QwertFactory = new QwertFactory();
        QwertSlaveSet listener = QwertFactory.createDianzongSlave(wrapper);

        // Add a few slave process images to the listener.
        listener.addProcessImage(getModscanProcessImage(1));
        //        listener.addProcessImage(getModscanProcessImage(2));
        listener.addProcessImage(getModscanProcessImage(3));
        //        listener.addProcessImage(getModscanProcessImage(5));
        listener.addProcessImage(getModscanProcessImage(9));

        // When the "listener" is started it will use the current thread to run. So, if an exception is not thrown
        // (and we hope it won't be), the method call will not return. Therefore, we start the listener in a separate
        // thread so that we can use this thread to modify the values.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listener.start();
                }
                catch (ModbusInitException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
            synchronized (listener) {
                listener.wait(200);
            }

            for (ProcessImage processImage : listener.getProcessImages())
                updateProcessImage((BasicProcessImage) processImage);
        }
    }

    static void updateProcessImage(BasicProcessImage processImage) throws IllegalDataAddressException {

        processImage.setNumeric(RegisterRange.INPUT_REGISTER, 20, DataType.FOUR_BYTE_FLOAT, ir1Value += 0.01);

        short hr1Value = processImage.getNumeric(RegisterRange.DIANZONG_REGISTER, 80, DataType.TWO_BYTE_BCD)
                .shortValue();
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 80, DataType.TWO_BYTE_BCD, hr1Value + 1);
    }

    static class BasicProcessImageListener implements ProcessImageListener {
        @Override
        public void coilWrite(int offset, boolean oldValue, boolean newValue) {
            System.out.println("Coil at " + offset + " was set from " + oldValue + " to " + newValue);
        }

        @Override
        public void holdingRegisterWrite(int offset, short oldValue, short newValue) {
            // Add a small delay to the processing.
            //            try {
            //                Thread.sleep(500);
            //            }
            //            catch (InterruptedException e) {
            //                // no op
            //            }
            System.out.println("HR at " + offset + " was set from " + oldValue + " to " + newValue);
        }
    }

    static BasicProcessImage getModscanProcessImage(int slaveId) {
        BasicProcessImage processImage = new BasicProcessImage(slaveId);
        //processImage.setAllowInvalidAddress(true);
        processImage.setInvalidAddressValue(Short.MIN_VALUE);

        processImage.setDianzongRegister(0, (short) 210);
        processImage.setDianzongRegister(1, (short) 500);
        processImage.setDianzongRegister(2, (short) 210);
        processImage.setDianzongRegister(3, (short) 500);
        processImage.setDianzongRegister(4, (short) 10000);
        processImage.setDianzongRegister(5, (short) 1);
        processImage.setDianzongRegister(6, (short) 10);
        processImage.setDianzongRegister(7, (short) 100);
        processImage.setDianzongRegister(8, (short) 1000);
        processImage.setDianzongRegister(9, (short) 10000);
        processImage.setDianzongRegister(10, (short) 1);
        processImage.setDianzongRegister(11, (short) 10);
        processImage.setDianzongRegister(12, (short) 100);
        processImage.setDianzongRegister(13, (short) 1000);
        processImage.setDianzongRegister(14, (short) 10000);
        processImage.setDianzongRegister(15, (short) 1);
        processImage.setDianzongRegister(16, (short) 10);
        processImage.setDianzongRegister(17, (short) 100);
        processImage.setDianzongRegister(18, (short) 1000);
        processImage.setDianzongRegister(19, (short) 10000);

        processImage.setDianzongRegister(20, (short) 1);
        processImage.setDianzongRegister(21, (short) 10);
        processImage.setDianzongRegister(22, (short) 100);
        processImage.setDianzongRegister(23, (short) 1000);
        processImage.setDianzongRegister(24, (short) 10000);

        processImage.setInputRegister(10, (short) 10000);
        processImage.setInputRegister(11, (short) 1000);
        processImage.setInputRegister(12, (short) 100);
        processImage.setInputRegister(13, (short) 10);
        processImage.setInputRegister(14, (short) 1);

/*        processImage.setBit(RegisterRange.DIANZONG_REGISTER, 15, 0, true);
        processImage.setBit(RegisterRange.DIANZONG_REGISTER, 15, 3, true);
        processImage.setBit(RegisterRange.DIANZONG_REGISTER, 15, 7, true);
        processImage.setBit(RegisterRange.DIANZONG_REGISTER, 15, 8, true);
        processImage.setBit(RegisterRange.DIANZONG_REGISTER, 15, 14, true);
*/
        processImage.setBit(RegisterRange.INPUT_REGISTER, 15, 0, true);
        processImage.setBit(RegisterRange.INPUT_REGISTER, 15, 7, true);
        processImage.setBit(RegisterRange.INPUT_REGISTER, 15, 8, true);
        processImage.setBit(RegisterRange.INPUT_REGISTER, 15, 15, true);
/*
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 16, DataType.TWO_BYTE_INT_SIGNED, new Integer(-1968));
        processImage
                .setNumeric(RegisterRange.DIANZONG_REGISTER, 17, DataType.FOUR_BYTE_INT_SIGNED, new Long(-123456789));
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 19, DataType.FOUR_BYTE_INT_SIGNED_SWAPPED, new Long(
                -123456789));
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 21, DataType.FOUR_BYTE_FLOAT, new Float(1968.1968));
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 23, DataType.EIGHT_BYTE_INT_SIGNED,
                new Long(-123456789));
*/
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 27, DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED, new Long(
                -123456789));
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 31, DataType.EIGHT_BYTE_FLOAT, new Double(1968.1968));

        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 80, DataType.TWO_BYTE_BCD, new Short((short) 1234));
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 81, DataType.FOUR_BYTE_BCD, new Integer(12345678));

        processImage.setString(RegisterRange.DIANZONG_REGISTER, 100, DataType.VARCHAR, 20, "Serotonin Software");

        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 12288, DataType.FOUR_BYTE_FLOAT_SWAPPED, new Float(
                1968.1968));
        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 12290, DataType.FOUR_BYTE_FLOAT_SWAPPED, new Float(
                -1968.1968));

        processImage.setNumeric(RegisterRange.INPUT_REGISTER, 16, DataType.TWO_BYTE_INT_UNSIGNED, new Integer(0xfff0));
        processImage
                .setNumeric(RegisterRange.INPUT_REGISTER, 17, DataType.FOUR_BYTE_INT_UNSIGNED, new Long(-123456789));
        processImage.setNumeric(RegisterRange.INPUT_REGISTER, 19, DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED, new Long(
                -123456789));
        processImage.setNumeric(RegisterRange.INPUT_REGISTER, 21, DataType.FOUR_BYTE_FLOAT_SWAPPED,
                new Float(1968.1968));
        processImage.setNumeric(RegisterRange.INPUT_REGISTER, 23, DataType.EIGHT_BYTE_INT_UNSIGNED,
                new Long(-123456789));
        processImage.setNumeric(RegisterRange.INPUT_REGISTER, 27, DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED, new Long(
                -123456789));
        processImage.setNumeric(RegisterRange.INPUT_REGISTER, 31, DataType.EIGHT_BYTE_FLOAT_SWAPPED, new Double(
                1968.1968));

        processImage.setNumeric(RegisterRange.DIANZONG_REGISTER, 50, DataType.EIGHT_BYTE_INT_UNSIGNED, 0);

        processImage.setString(RegisterRange.INPUT_REGISTER, 100, DataType.CHAR, 15, "Software de la Serotonin");

        processImage.setExceptionStatus((byte) 151);

        // Add an image listener.
        processImage.addListener(new BasicProcessImageListener());

        return processImage;
    }
}

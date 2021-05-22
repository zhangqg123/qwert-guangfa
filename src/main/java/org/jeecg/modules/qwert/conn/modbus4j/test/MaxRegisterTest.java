package org.jeecg.modules.qwert.conn.modbus4j.test;

import org.jeecg.modules.qwert.conn.modbus4j.source.BatchRead;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusFactory;
import org.jeecg.modules.qwert.conn.modbus4j.source.ModbusMaster;
import org.jeecg.modules.qwert.conn.modbus4j.source.code.DataType;
import org.jeecg.modules.qwert.conn.modbus4j.source.code.RegisterRange;
import org.jeecg.modules.qwert.conn.modbus4j.source.locator.StringLocator;

public class MaxRegisterTest {
    public static void main(String[] args) {
        BatchRead<Integer> batch;
        int index;
        boolean contiguous = true;

        ModbusMaster master = new ModbusFactory().createUdpMaster(null);
        master.setMaxReadBitCount(100);
        master.setMaxReadRegisterCount(11);

        batch = new BatchRead<Integer>();
        index = 0;
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 50, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 100, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 150, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 200, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 250, DataType.CHAR, 50));
        batch.setContiguousRequests(contiguous);
        batch.getReadFunctionGroups(master);

        batch = new BatchRead<Integer>();
        index = 0;
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.CHAR, 49));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 50, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 100, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 150, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 200, DataType.CHAR, 49));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 250, DataType.CHAR, 50));
        batch.setContiguousRequests(contiguous);
        batch.getReadFunctionGroups(master);

        batch = new BatchRead<Integer>();
        index = 0;
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 0, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 50, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 100, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 100, DataType.CHAR, 25));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 150, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 200, DataType.CHAR, 50));
        batch.addLocator(index++, new StringLocator(1, RegisterRange.HOLDING_REGISTER, 250, DataType.CHAR, 50));
        batch.setContiguousRequests(contiguous);
        batch.getReadFunctionGroups(master);
    }
}

package org.jeecg.modules.qwert.conn.qudong.locator;

import org.jeecg.modules.qwert.conn.qudong.base.QwertUtils;
import org.jeecg.modules.qwert.conn.qudong.code.DataType;
import org.jeecg.modules.qwert.conn.qudong.code.RegisterRange;
import org.jeecg.modules.qwert.conn.qudong.exception.QwertIdException;
import org.jeecg.modules.qwert.conn.qudong.sero.NotImplementedException;

/**
 * <p>BinaryLocator class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class BinaryLocator extends BaseLocator<Boolean> {
    private int bit = -1;

    /**
     * <p>Constructor for BinaryLocator.</p>
     *
     * @param slaveId a int.
     * @param range a int.
     * @param offset a int.
     */
    public BinaryLocator(int slaveId, int range, int offset) {
        super(slaveId, range, offset);
        if (!isBinaryRange(range))
            throw new QwertIdException("Non-bit requests can only be made from coil status and input status ranges");
        validate();
    }

    /**
     * <p>Constructor for BinaryLocator.</p>
     *
     * @param slaveId a int.
     * @param range a int.
     * @param offset a int.
     * @param bit a int.
     */
    public BinaryLocator(int slaveId, int range, int offset, int bit) {
        super(slaveId, range, offset);
        if (isBinaryRange(range))
            throw new QwertIdException("Bit requests can only be made from holding registers and input registers");
        this.bit = bit;
        validate();
    }

    /**
     * <p>isBinaryRange.</p>
     *
     * @param range a int.
     * @return a boolean.
     */
    public static boolean isBinaryRange(int range) {
        return range == RegisterRange.COIL_STATUS || range == RegisterRange.INPUT_STATUS;
    }

    /**
     * <p>validate.</p>
     */
    protected void validate() {
        super.validate(1);

        if (!isBinaryRange(range))
            QwertUtils.validateBit(bit);
    }

    /**
     * <p>Getter for the field <code>bit</code>.</p>
     *
     * @return a int.
     */
    public int getBit() {
        return bit;
    }

    /** {@inheritDoc} */
    @Override
    public int getDataType() {
        return DataType.BINARY;
    }

    /** {@inheritDoc} */
    @Override
    public int getRegisterCount() {
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "BinaryLocator(slaveId=" + getSlaveId() + ", range=" + range + ", offset=" + offset + ", bit=" + bit
                + ")";
    }

    /** {@inheritDoc} */
    @Override
    public Boolean bytesToValueRealOffset(byte[] data, int offset) {
        // If this is a coil or input, convert to boolean.
        if (range == RegisterRange.COIL_STATUS || range == RegisterRange.INPUT_STATUS)
            return new Boolean((((data[offset / 8] & 0xff) >> (offset % 8)) & 0x1) == 1);

        // For the rest of the types, we double the normalized offset to account for short to byte.
        offset *= 2;

        // We could still be asking for a binary if it's a bit in a register.
        return new Boolean((((data[offset + 1 - bit / 8] & 0xff) >> (bit % 8)) & 0x1) == 1);
    }

    /** {@inheritDoc} */
    @Override
    public short[] valueToShorts(Boolean value) {
        throw new NotImplementedException();
    }
}

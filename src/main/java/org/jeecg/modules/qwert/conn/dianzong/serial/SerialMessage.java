package org.jeecg.modules.qwert.conn.dianzong.serial;

import org.jeecg.modules.qwert.conn.dianzong.msg.QwertMessage;

/**
 * <p>Abstract SerialMessage class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
abstract public class SerialMessage {
    protected final QwertMessage QwertMessage;

    /**
     * <p>Constructor for SerialMessage.</p>
     *
     * @param QwertMessage a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertMessage} object.
     */
    public SerialMessage(QwertMessage QwertMessage) {
        this.QwertMessage = QwertMessage;
    }

    /**
     * <p>Getter for the field <code>QwertMessage</code>.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.conn.dianzong.msg.QwertMessage} object.
     */
    public QwertMessage getQwertMessage() {
        return QwertMessage;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SerialMessage [QwertMessage=" + QwertMessage + "]";
    }
}

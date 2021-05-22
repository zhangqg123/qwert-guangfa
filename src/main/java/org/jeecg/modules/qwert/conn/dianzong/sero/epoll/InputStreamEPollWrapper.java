/**
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package org.jeecg.modules.qwert.conn.dianzong.sero.epoll;

import java.io.InputStream;

/**
 * <p>InputStreamEPollWrapper interface.</p>
 *
 * @author Terry Packer
 * @version 5.0.0
 */
public interface InputStreamEPollWrapper {

	/**
	 * <p>add.</p>
	 *
	 * @param in a {@link java.io.InputStream} object.
	 * @param inputStreamCallback a {@link org.jeecg.modules.qwert.conn.dianzong.sero.epoll.Modbus4JInputStreamCallback} object.
	 */
	void add(InputStream in, Modbus4JInputStreamCallback inputStreamCallback);

	/**
	 * <p>remove.</p>
	 *
	 * @param in a {@link java.io.InputStream} object.
	 */
	void remove(InputStream in);

}

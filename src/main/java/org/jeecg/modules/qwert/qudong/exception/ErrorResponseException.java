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
package org.jeecg.modules.qwert.qudong.exception;

import org.jeecg.modules.qwert.qudong.msg.QwertRequest;
import org.jeecg.modules.qwert.qudong.msg.QwertResponse;

/**
 * <p>ErrorResponseException class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class ErrorResponseException extends Exception {
    private static final long serialVersionUID = -1;

    private final QwertRequest originalRequest;
    private final QwertResponse errorResponse;

    /**
     * <p>Constructor for ErrorResponseException.</p>
     *
     * @param originalRequest a {@link org.jeecg.modules.qwert.qudong.msg.QwertRequest} object.
     * @param errorResponse a {@link org.jeecg.modules.qwert.qudong.msg.QwertResponse} object.
     */
    public ErrorResponseException(QwertRequest originalRequest, QwertResponse errorResponse) {
        this.originalRequest = originalRequest;
        this.errorResponse = errorResponse;
    }

    /**
     * <p>Getter for the field <code>errorResponse</code>.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.qudong.msg.QwertResponse} object.
     */
    public QwertResponse getErrorResponse() {
        return errorResponse;
    }

    /**
     * <p>Getter for the field <code>originalRequest</code>.</p>
     *
     * @return a {@link org.jeecg.modules.qwert.qudong.msg.QwertRequest} object.
     */
    public QwertRequest getOriginalRequest() {
        return originalRequest;
    }

    /** {@inheritDoc} */
    @Override
    public String getMessage() {
        return errorResponse.getExceptionMessage();
    }
}

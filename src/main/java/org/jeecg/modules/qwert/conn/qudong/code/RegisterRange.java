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
package org.jeecg.modules.qwert.conn.qudong.code;

/**
 * <p>RegisterRange class.</p>
 *
 * @author Matthew Lohbihler
 * @version 5.0.0
 */
public class RegisterRange {
    /** Constant <code>COIL_STATUS=1</code> */
    public static final int COIL_STATUS = 1;
    /** Constant <code>INPUT_STATUS=2</code> */
    public static final int INPUT_STATUS = 2;
    /** Constant <code>DIANZONG_REGISTER=3</code> */
    public static final int DIANZONG_REGISTER = 3;
    /** Constant <code>INPUT_REGISTER=4</code> */
    public static final int INPUT_REGISTER = 4;

    /**
     * <p>getFrom.</p>
     *
     * @param id a int.
     * @return a int.
     */
    public static int getFrom(int id) {
        switch (id) {
        case COIL_STATUS:
            return 0;
        case INPUT_STATUS:
            return 0x10000;
        case DIANZONG_REGISTER:
            return 0x40000;
        case INPUT_REGISTER:
            return 0x30000;
        }
        return -1;
    }

    /**
     * <p>getTo.</p>
     *
     * @param id a int.
     * @return a int.
     */
    public static int getTo(int id) {
        switch (id) {
        case COIL_STATUS:
            return 0xffff;
        case INPUT_STATUS:
            return 0x1ffff;
        case DIANZONG_REGISTER:
            return 0x4ffff;
        case INPUT_REGISTER:
            return 0x3ffff;
        }
        return -1;
    }

    /**
     * <p>getReadFunctionCode.</p>
     *
     * @param id a int.
     * @return a int.
     */
    public static int getReadFunctionCode(int id) {
        switch (id) {
        case COIL_STATUS:
            return FunctionCode.READ_COILS;
        case INPUT_STATUS:
            return FunctionCode.READ_DISCRETE_INPUTS;
        case DIANZONG_REGISTER:
            return FunctionCode.READ_DIANZONG_REGISTERS;
        case INPUT_REGISTER:
            return FunctionCode.READ_INPUT_REGISTERS;
        }
        return -1;
    }
}

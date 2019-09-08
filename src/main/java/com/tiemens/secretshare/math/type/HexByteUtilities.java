/*******************************************************************************
 * Copyright (c) 2009, 2014 Tim Tiemens.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *
 * Contributors:
 *     Tim Tiemens - initial API and implementation
 *******************************************************************************/
package com.tiemens.secretshare.math.type;

public final class HexByteUtilities
{

    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================


    public static byte[] hexToBytes(char[] hex)
    {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++)
        {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127)
            {
                value -= 256;
            }
            raw[i] = (byte) value;
        }
        return raw;
    }

    public static byte[] hexToBytes(String hex)
    {
      return hexToBytes(hex.toCharArray());
    }

    private static String[] lookup = { "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "A", "B", "C", "D", "E", "F"
    };

    private static String byteToHexString(byte b)
    {
        String ret = "";
        byte ch = 0x00;
        ch = (byte) (b & 0xF0); // strip off high
        ch = (byte) (ch >>> 4); // shift
        ch = (byte) (ch & 0X0F); // the >>> turned on high bits, get rid of them
        ret += lookup[ch];

        ch = (byte) (b & 0X0F);
        ret += lookup[ch];

        return ret;
    }

    public static String printAsHex(byte[] bytes)
    {
        return bytesToHexString(bytes);
    }

    public static String bytesToHexString(byte[] bytes)
    {
        String ret = "";
        for (byte b : bytes)
        {
            ret += byteToHexString(b);
        }
        return ret;
    }


    // ==================================================
    // instance data
    // ==================================================

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    private HexByteUtilities()
    {
        // no instances
    }

    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================
}

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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class HexByteUtiltiesTest
{
    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    // ==================================================
    // public methods
    // ==================================================
    @Test
    public void testHexToString()
    {
        sub("9A1033", 0x9a, 0x10, 0x33);
        sub("FEFF8311", 0xFE, 0xff, 0x83, 0x11);
    }

    private void sub(String expected,
                     int... list)
    {
        byte[] bytes = new byte[list.length];
        for (int i = 0, n = list.length; i < n; i++)
        {
            bytes[i] = int2byte(list[i]);
        }
        String bytesAsHex = BigIntStringChecksum.bytesToHexString(bytes);
        assertEquals(expected, bytesAsHex);

    }

    private byte int2byte(int i)
    {
        if ((i >= 0) && (i <= Byte.MAX_VALUE))
        {
            return (byte) i;
        }
        else if (i >= Byte.MIN_VALUE)
        {
            return (byte) i;
            //return (byte) (i + Byte.MAX_VALUE);
        }
        else
        {
            throw new RuntimeException("Invalid argument, i=" + i);
        }
    }

    @Test
    public void testIt()
        throws Exception
    {
        subTestCombinations("", "54696d", "5532013");
        subTestCombinations("", "00", "0");
    }
    private void subTestCombinations(String description,
                                     String asHex,
                                     String asBigInteger)
        throws Exception
    {
        final String utf8 = "UTF8";
        byte[] hexbytes = HexByteUtilities.hexToBytes(asHex);
        String fromHex = new String(hexbytes, utf8);
        BigInteger biginteger = new BigInteger(asBigInteger);
        String fromBigInteger = BigIntUtilities.Human.createHumanString(biginteger);
        System.out.println("fromHex(" + asHex + "):  hex='" +
                           HexByteUtilities.printAsHex(fromHex.getBytes(utf8)) +
                           "' orighex='" + HexByteUtilities.printAsHex(hexbytes) + "'");
        System.out.println("fromBin:  hex='" +
                           HexByteUtilities.printAsHex(fromBigInteger.getBytes(utf8)) +
                           "' biginteger=" + biginteger);
        assertEquals(fromHex, fromBigInteger, description);
    }

    // ==================================================
    // non public methods
    // ==================================================

}

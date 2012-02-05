/*******************************************************************************
 * $Id: $
 * Copyright (c) 2009-2010 Tim Tiemens.
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
 ******************************************************************************/
package com.tiemens.secretshare.math;

import java.math.BigInteger;

import junit.framework.Assert;
import junit.framework.TestCase;

public class HexByteUtiltiesUT
    extends TestCase
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

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    // ==================================================
    // public methods
    // ==================================================
    public void testHexToString()
    {
        sub("9A1033", 0x9a, 0x10, 0x33);
        sub("FEFF8311", 0xFE, 0xff, 0x83, 0x11);
    }
    
    private void sub(String expected,
                     int ... list)
    {
        byte[] bytes = new byte[list.length];
        for (int i = 0, n = list.length; i < n; i++)
        {
            bytes[i] = int2byte(list[i]);
        }
        String bytesAsHex = BigIntStringChecksum.bytesToHexString(bytes);
        Assert.assertEquals(expected, bytesAsHex);
        
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
        final String UTF8 = "UTF8";
        byte[] hexbytes = HexByteUtilities.hexToBytes(asHex);
        String fromHex = new String(hexbytes, UTF8);
        BigInteger biginteger = new BigInteger(asBigInteger);
        String fromBigInteger = BigIntUtilities.Human.createHumanString(biginteger);
        System.out.println("fromHex(" + asHex + "):  hex='" +
                           HexByteUtilities.printAsHex(fromHex.getBytes(UTF8)) +
                           "' orighex='" + HexByteUtilities.printAsHex(hexbytes) + "'");
        System.out.println("fromBin:  hex='" +
                           HexByteUtilities.printAsHex(fromBigInteger.getBytes(UTF8)) +
                           "' biginteger=" + biginteger);
        Assert.assertEquals(description, fromHex, fromBigInteger);
    }
    // ==================================================
    // non public methods
    // ==================================================


}

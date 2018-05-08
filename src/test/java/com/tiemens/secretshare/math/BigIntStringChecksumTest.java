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
package com.tiemens.secretshare.math;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.tiemens.secretshare.exceptions.SecretShareException;

public class BigIntStringChecksumTest
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

    /**
     * Test negative BigInteger values.
     */
    @Test
    public void testNegativeBigInteger()
    {
        final int val = -100;
        BigInteger bint = BigInteger.valueOf(val);
        String s = BigIntStringChecksum.create(bint).toString();
        /// System.out.println("val=" + bint + " string='" + s + "'");
        BigInteger read = BigIntStringChecksum.fromString(s).asBigInteger();
        Assert.assertEquals(bint, read);

        // now test the checksum:
        subtestGood(-100, "bigintcs:-000064-BBC6EC");
        // now, drop that internal "-" and make sure it throws an exception:
        subtestBad("bigintcs:000064-BBC6EC");
    }

    /**
     * Test some random strings.
     */
    @Test
    public void testRandomBad()
    {
        subtestBad("bigintcs:0004-BBEC");
        subtestBad("bigintcs:0004-BBECEF");
        subtestBad("bigintcs:000004-BBECEF");
    }

    /**
     * Test having an extra set of "0"s [with the proper checksum].
     */
    @Test
    public void testLotsOfLeadingZeros()
    {
        String s = "bigintcs:-000000-000064-F913AE";
        BigInteger b = BigIntStringChecksum.fromString(s).asBigInteger();
        Assert.assertNotNull(b);
        s = "bigintcs:-000064-BBC6EC";
        Assert.assertEquals("leading 0s different value",
                            b,
                            BigIntStringChecksum.fromString(s).asBigInteger());
    }

    /**
     * Test some crazy strings [that have the proper checksum]
     * that don't actually result in BigIntegers.
     */
    @Test
    public void testReallyBadInputs()
    {
        // true = generates 'reallyBad'[odd] string     false = run tests
        boolean runAsGenerate = false;

        String[] reallyBad = new String[] {
                "AB##",     "461C44",
                "8f4...",   "1750A6",
                "$",        "7DE9C3",
                "00abcO",   "E5D568",          // that is abc-letter-O not abc-zero
        };

        for (int i = 0, n = reallyBad.length; i < n; i += 2)
        {
            if (runAsGenerate)
            {
                // This path generates the strings above
                String s = reallyBad[i];
                System.out.println("\"" + s + "\",    \"" +
                                   BigIntStringChecksum.computeMd5ChecksumLimit6(s) + "\",");
            }
            else
            {
                // This path checks the strings above

                BigIntStringChecksum bisc = new BigIntStringChecksum(reallyBad[i],
                                                                     reallyBad[i + 1]);
                try
                {
                    bisc.asBigInteger();   // should throw exception
                    Assert.fail("Really bad input '" + reallyBad[i] + "' failed to fail");
                }
                catch (SecretShareException e)
                {
                    // we don't need to see every stack trace:
                    // e.printStackTrace();
                }
            }
        }
    }

    /**
     * Test a bunch of random BigInteger-to-string-backto-BigInteger:
     */
    @Test
    public void testRandomCoversions()
    {
        Random random = new SecureRandom();
        byte[] bytes = new byte[20];
        for (int i = 0, n = 5000; i < n; i++)
        {
            random.nextBytes(bytes);
            BigInteger bi = new BigInteger(bytes);
            // System.out.println(bi);
            subtestGood(bi);
        }
    }


    /**
     * Test from the documentation on the leading "-".
     * i.e. make sure the leading "-" is part of the checksum computation.
     *
     */
    @Test
    public void testLeadingDash()
    {
        final String sameValue = "000064";
        final String sameChecksum = "-BBC6EC";

        // insert a leading "-", parse is OK:
        BigInteger okNeg100 = BigIntStringChecksum.fromString("bigintcs:" +
                       "-" + sameValue + sameChecksum).asBigInteger();
        Assert.assertEquals("-100 failed", new BigInteger("-100"), okNeg100);

        // without a leading "-", parse FAILS:
        try
        {
            BigInteger failNeg100 = BigIntStringChecksum.fromString("bigintcs:" + "" +
                       sameValue + sameChecksum).asBigInteger();
            Assert.fail("100 has checksum error, but did not throw exception: " + failNeg100);
        }
        catch (SecretShareException e)
        {
            // ok
        }
    }

    @Test
    public void testfromStringOrNull()
    {
        assertNull(BigIntStringChecksum.fromStringOrNull(null));
        assertNull(BigIntStringChecksum.fromStringOrNull("invalid"));

        // completely wrong format
        assertNull(BigIntStringChecksum.fromStringOrNull("bigintcs:" + "001" +  "-BBC"));

        // completely invalid value
        assertNull(BigIntStringChecksum.fromStringOrNull("bigintcs:" + "001234" +  "-BBC6EC"));

        // positive versus negative - the checksum only works for the negative
        assertNull(BigIntStringChecksum.fromStringOrNull("bigintcs:" + "000064" +  "-BBC6EC"));
        assertNotNull(BigIntStringChecksum.fromStringOrNull("bigintcs:-000064-BBC6EC"));
    }


    // ==================================================
    // non public methods
    // ==================================================

    private void subtestBad(String s)
    {
        try
        {
            // 1st test: this one should throw an exception:
            BigIntStringChecksum bics = BigIntStringChecksum.fromString(s);
            Assert.fail("Checksum failed to throw exception on s='" + s + "' bint=" + bics);
        }
        catch (SecretShareException e)
        {
            // ok, correct

            // 2nd test: this one should NOT throw an exception; it should return null:
            BigIntStringChecksum mustbenull = BigIntStringChecksum.fromStringOrNull(s);
            Assert.assertNull(mustbenull);
        }
    }


    private void subtestGood(BigInteger i)
    {
        String s = BigIntStringChecksum.create(i).toString();
        subtestGood(i, s);
    }
    private void subtestGood(int i,
                             String s)
    {
        subtestGood(BigInteger.valueOf(i), s);
    }
    private void subtestGood(BigInteger expected,
                             String s)
    {
        BigInteger bint = BigIntStringChecksum.fromString(s).asBigInteger();
        Assert.assertEquals(expected, bint);
    }


    public void testJustPrintAsBigIntCs()
    {
        String in = "11753999";
        //in = "124332";
        String s = BigIntStringChecksum.create(new BigInteger(in)).toString();
        System.out.println(in + "=" + s);
    }
}

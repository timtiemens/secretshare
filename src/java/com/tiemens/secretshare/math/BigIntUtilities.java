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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import com.tiemens.secretshare.exceptions.SecretShareException;

public class BigIntUtilities
{


    // ==================================================
    // class static data
    // ==================================================
    private static final String UTF8 = "UTF-8";


    // ==================================================
    // class static methods
    // ==================================================

    public static class Human
    {
        /**
         * Convert a "human string" into a BigInteger by using the string's
         *   byte[] array.
         * This is NOT the same as new BigInteger("string").
         * 
         * @param in a string like "This is a secret" or "123FooBar"
         * @return BigInteger
         */
        public static BigInteger createBigInteger(final String in)
        {
            BigInteger ret = null;
            try
            {
                byte[] b = in.getBytes(UTF8);
                ret = new BigInteger(b);
                return ret;
            }
            catch (UnsupportedEncodingException e)
            {
                // just can't happen, but if it does...
                e.printStackTrace();
                return null;
            }
        }
        
        /**
         * @param in the BigInteger whose bytes to use for the String
         *      usually the output of 'createBigInteger()', above.
         * @return String-ified BigInteger.bytes[]
         */
        public static String createHumanString(final BigInteger in)
        {
            try
            {
                byte[] b = in.toByteArray();
                String s = new String(b, UTF8);
                return s;
            }
            catch (UnsupportedEncodingException e)
            {
                // just can't happen, but if it does...
                e.printStackTrace();
                return null;
            }
        }
    } 
        
    public static class Checksum
    {
        /**
         * @param value string to test
         * @return true if this value is a big-int-checksum string 
         *              (i.e. starts with "bigintcs:")
         *         false otherwise
         */
        public static boolean couldCreateFromStringMd5CheckSum(String value)
        {
            return BigIntStringChecksum.startsWithPrefix(value);
        }


        /**
         * @param hexStringWithMd5sum the bigintcs:hhhhh-CCCCCC string representation
         * @return the bigintstringchecksum
         * @throws SecretShareException on error
         */
        public static BigIntStringChecksum createBiscs(final String hexStringWithMd5sum)
        {
            return BigIntStringChecksum.fromString(hexStringWithMd5sum);
        }

        /**
         * @param hexStringWithMd5sum the bigintcs:hhhhh-CCCCCC string representation
         * @return the biginteger
         * @throws SecretShareException on error
         */
        public static BigInteger createBigInteger(final String hexStringWithMd5sum)
        {
            return createBiscs(hexStringWithMd5sum).asBigInteger();
        }
        /**
         * @param in BigInteger to convert
         * @return the bigintcs:hhhhh-CCCCCC string representation
         */
        public static String createMd5CheckSumString(final BigInteger in)
        {
            return BigIntStringChecksum.create(in).toString();
        }
    }


    public static class Hex
    {
        /**
         * @param value string to test
         * @return true if this value is a hex-encoded string (i.e. starts with "0x")
         */
        public static boolean couldCreateFromStringHex(String value)
        {
            if ((value != null) && (value.length() >= 2))
            {
                if (value.substring(0, 2).equalsIgnoreCase("0x"))
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param value string as hex-encoded number
         * @return BigInteger
         */
        public static BigInteger createBigInteger(String value)
        {
            final int HEX_RADIX = 16;
            if (value == null)
            {
                throw new SecretShareException("value cannot be null");
            }
            if (couldCreateFromStringHex(value))
            {
                String after = value.substring(2);
                try
                {
                    return new BigInteger(after, HEX_RADIX);
                }
                catch (NumberFormatException e)
                {
                    throw new SecretShareException("Hex parse failed for '" + value + "'");
                }
            }
            else
            {
                throw new SecretShareException("value must start with '0x' (input='" + value + "')");
            }
        }

        public static String createHexString(BigInteger bigInteger)
        {
            final int HEX_RADIX = 16;
            return "0x" + bigInteger.toString(HEX_RADIX);
        }
    }        
        
        

    public static BigInteger createPrimeBigger(BigInteger valueThatDeterminesNumberOfBits)
    {
        int numbits = valueThatDeterminesNumberOfBits.bitLength() + 1;
        Random random = new SecureRandom();
        BigInteger ret = BigInteger.probablePrime(numbits, random);
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

    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================
}

package com.tiemens.secretshare.math;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import com.tiemens.secretshare.exceptions.SecretShareException;

public class BigIntUtilities
{


    // ==================================================
    // class static data
    // ==================================================


    // ==================================================
    // class static methods
    // ==================================================

    /**
     * Convert a "human string" into a BigInteger by using the string's
     *   byte[] array.
     * This is NOT the same as new BigInteger("string").
     * 
     * @param in a string like "This is a secret" or "123FooBar"
     * @return BigInteger
     */
    public static BigInteger createFromStringsBytesAsData(final String in)
    {
        BigInteger ret = null;
        byte[] b = in.getBytes();
        ret = new BigInteger(b);
        return ret;
    }
    
    /**
     * @param in the BigInteger whose bytes to use for the String
     * @return String-ified BigInteger.bytes[]
     */
    public static String createStringFromBigInteger(final BigInteger in)
    {
       byte[] b = in.toByteArray();
       String s = new String(b);
       return s;
    }
    

    
    /**
     * @param in biginteger to convert
     * @return the bigintcs:hhhhh-CCCCCC string representation
     */
    public static String createStringMd5CheckSumFromBigInteger(final BigInteger in)
    {
        return BigIntStringChecksum.create(in).toString();

    }


    /**
     * @param value to test
     * @return true if this value is a big-int-checksum string (i.e. starts with "bigintcs:")
     */
    public static boolean couldCreateFromStringMd5CheckSum(String value)
    {
        return BigIntStringChecksum.startsWithPrefix(value);
    }

    /**
     * @param hexStringWithMd5sum the bigintcs:hhhhh-CCCCCC string representation
     * @return the biginteger
     * @throws SecretShareException on error
     */
    public static BigInteger createFromStringMd5CheckSum(final String hexStringWithMd5sum)
    {
        return BigIntStringChecksum.fromString(hexStringWithMd5sum).asBigInteger();
    }




    public static BigInteger createPrimeBigger(BigInteger secret)
    {
        int numbits = secret.bitLength() + 1;
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
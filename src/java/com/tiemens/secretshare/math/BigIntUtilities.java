package com.tiemens.secretshare.math;

import java.math.BigInteger;

public class BigIntUtilities
{


    // ==================================================
    // class static data
    // ==================================================


    // ==================================================
    // class static methods
    // ==================================================

    public static BigInteger createFromString(final String in)
    {
        BigInteger ret = null;
        byte[] b = in.getBytes();
        ret = new BigInteger(b);
        return ret;
    }

    // TODO: get rid of this method
    public static String createStringMd5CheckSumFromBigInteger(final BigInteger in)
    {

        return BigIntStringChecksum.create(in).toString();

    }
    // TODO: get rid of this method
    public static BigInteger createFromStringMd5CheckSum(final String hexStringWithMd5sum)
    {
        return BigIntStringChecksum.fromString(hexStringWithMd5sum).asBigInteger();
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
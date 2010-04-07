package com.tiemens.secretshare.math;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import com.tiemens.secretshare.exceptions.SecretShareException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class BigIntStringChecksumUT
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
    
    public void testRandomBad()
    {
        subtestBad("bigintcs:0004-BBEC");
        subtestBad("bigintcs:0004-BBECEF");
        subtestBad("bigintcs:000004-BBECEF");
    }

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
    private void subtestBad(String s)
    {
        try
        {
            BigIntStringChecksum bics = BigIntStringChecksum.fromString(s);
            Assert.fail("Checksum failed to throw exception on s='" + s + "' bint=" + bics);
        }
        catch (SecretShareException e)
        {
            // ok, correct
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
    private void subtestGood(BigInteger i,
                             String s)
    {
        BigInteger bint = BigIntStringChecksum.fromString(s).asBigInteger();
        Assert.assertEquals(i, bint);
    }
    
 
    // ==================================================
    // non public methods
    // ==================================================
    
    
}
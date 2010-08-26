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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

public class BigIntUtilitiesUT
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

    public void testBasic()
    {
        subtest("T", BigInteger.valueOf(                                     84L));
        subtest("Tim", BigInteger.valueOf((84L * 256 * 256) + (105L * 256) + (109)));

    }
    
    // 012345678901234567891234
    public void testFixedPrimes()
    {
        // This big integer passed 10,000 iterations check and was created with
        //  "probablePrime(192-bits)"
        BigInteger p192bits = 
            new BigInteger("4482452815678181799315042254392791676748504508225749365861");
        String cs = BigIntUtilities.Checksum.createMd5CheckSumString(p192bits);
        String csexpected = /*"42" + */ "1CB9AC";
        String expected =
            BigIntStringChecksum.PREFIX_BIGINT_DASH_CHECKSUM +
            "b6cefd-b34d49-3b20e5-d6f4a3-ec9c67-427b48-46bb1b-bdb465-" + csexpected;

        Assert.assertEquals("expected='" + expected + "' actual='" + cs + "'", 
                            expected, cs);
        

        // This big integer was created with probablePrime(194-bits)
        BigInteger p194bits = 
            new BigInteger("14976407493557531125525728362448106789840013430353915016137");
        cs = BigIntUtilities.Checksum.createMd5CheckSumString(p194bits);
        expected = 
            "bigintcs:000002-62c8fd-6ec81b-3c0584-136789-80ad34-9269af-da237f-8ff3c9-12BCCD";
        Assert.assertEquals("expected='" + expected + "' actual='" + cs + "'", 
                            expected, cs);
        
        // is this actually bigger than 2^192?
        BigInteger two192 = BigInteger.valueOf(2).pow(192);
        Assert.assertTrue("Not actually bigger", p194bits.compareTo(two192) == 1);
        
        if (false)
        {
            // 10000 iterations takes 3 seconds:
            Assert.assertTrue(passesMillerRabin(p194bits, 10000, null));
        }
        
        
        //BigInteger p386bits = BigInteger.probablePrime(386, new SecureRandom());
        BigInteger p386bits = new BigInteger("830856716641269388050926147210" +
                                             "378437007763661599988974204336" +
                                             "741171904442622602400099072063" +
                                             "84693584652377753448639527");
        cs = BigIntUtilities.Checksum.createMd5CheckSumString(p386bits);
        System.out.println("p386=" + p386bits);
        System.out.println("p386=" + cs);
        expected = 
            "bigintcs:000002-1bd189-52959f-874f79-3d6cf5-11ac82-e6cea4-46c19c-5f523a-5318c7-" +
            "e0f379-66f9e1-308c61-2d8d0b-dba253-6f54b0-ec6c27-3198DB";

        Assert.assertEquals("expected='" + expected + "' actual='" + cs + "'", 
                            expected, cs);
        if (false)
        {
            // 10000 iterations takes 51 seconds for 386 bits:
            // 100000 iterations takes 392 seconds for 386 bits:
            Assert.assertTrue(passesMillerRabin(p386bits, 10000, null));
        }
        

    }
    public void testStringMd5Conversions()
    {
        Random random = new SecureRandom();
        int n = 100;  
        for (int i = 0; i < n; i++)
        {
            BigInteger bi = BigInteger.probablePrime(100, random);
            String bics = BigIntUtilities.Checksum.createMd5CheckSumString(bi);
            BigInteger read = BigIntUtilities.Checksum.createBigInteger(bics);
            Assert.assertEquals(bi, read);
            
        }
        
        for (int i = -500; i < 5000; i++)
        {
            BigInteger bi = BigInteger.valueOf(i);
            String bics = BigIntUtilities.Checksum.createMd5CheckSumString(bi);
            BigInteger read = BigIntUtilities.Checksum.createBigInteger(bics);
            Assert.assertEquals(bi, read);
        }
        
        for (int c = 0, i = -123456789; c < 10; c++, i--)
        {
            BigInteger bi = BigInteger.valueOf(i);
            String bics = BigIntUtilities.Checksum.createMd5CheckSumString(bi);
            // make sure it is "big enough" to get into 2nd "bucket"
            // i.e. "bigintcs:-123456-123456-ABCDEF"  is good enough for test
            //      "bigintcs:-789ABC-ABCDEF"         is not a good enough test
            Assert.assertTrue("size is wrong", bics.length() > 24);
            
            BigInteger read = BigIntUtilities.Checksum.createBigInteger(bics);
            Assert.assertEquals("big negative failed, i=" + i,
                                bi, read);            
        }
    }
    public void testBitsPrime() 
        throws NoSuchAlgorithmException
    {
        final int bits = 194;  // 24 characters plus a little extra

        Random random = new SecureRandom(); // let the system pick our provider
        BigInteger bi = BigInteger.probablePrime(bits, random);
        System.out.println("ProbablePrime(" + bits + ")=" + bi);
        System.out.println("                  =" + bi.toString(16));
        System.out.println("                  =" + 
                           BigIntUtilities.Checksum.createMd5CheckSumString(bi));
        System.out.println("  bitlength=" + bi.bitLength());
        System.out.println("  bitcount =" + bi.bitCount());
        final int certainty = Integer.MAX_VALUE; //10000000;
        if (true)
        {
            if (! bi.isProbablePrime(certainty))
            {
                System.out.println("***** did not pass certainty=" + certainty);
            }
            else
            {
                System.out.println("passed certainty " + certainty);
            }
        }
        if (false)
        {
            // this takes 2+ seconds with 10,000 iterations

            int iterations = 10000;
            final long start = new java.util.Date().getTime();
            if (! passesMillerRabin(bi, iterations, null))
            {
                System.out.println("***** did not pass iterations=" + iterations);
            }
            else
            {
                System.out.println("passed iterations " + iterations);
            }
        
            final long stop = new java.util.Date().getTime();
            System.out.println("Iterations, time elapsed=" + (stop - start));
        }
    }
    public void testModExample()
    {
        BigInteger original = new BigInteger("14976407493557530648572935587878871741988301238449789883337");
        BigInteger divideby = new BigInteger("1658880");
        BigInteger modulus =  new BigInteger("14976407493557531125525728362448106789840013430353915016137");
        BigInteger expected = new BigInteger("14976407493557531125525440847502616718686555765114601802327");
        
        
        BigInteger modorig = original.multiply(modulus);
        BigInteger result = modorig.divide(divideby);
        result = result.mod(modulus);
        
        BigInteger remainder = modorig.subtract(result.multiply(divideby));
        
        System.out.println("Expected = " + expected);
        System.out.println("Actual   = " + result);
        Assert.assertEquals("Foo, remainder=" + remainder, expected, result);
    }
    public void testBigMultiply()
    {
        Random rand = new SecureRandom();
        BigInteger one = BigInteger.probablePrime(120, rand);
        BigInteger two = BigInteger.probablePrime(120, rand);
        
        BigInteger mult = one.multiply(two);
        System.out.println("Multiply");
        System.out.println("a =" + one);
        System.out.println("b =" + two);
        System.out.println("ab=" + mult);
        
        one = new BigInteger("844611759251583530726208571155127753");
        two = new BigInteger("1229148547440409489619281705118933783");
        BigInteger multex = new BigInteger("1038153317035172738059682655695039749481297254608774273985162888512579599");
        
        Assert.assertEquals(multex, one.multiply(two));
        
    }
    // ==================================================
    // non public methods
    // ==================================================

    private void subtest(String in,
                         BigInteger expected)
    {
        BigInteger actual = BigIntUtilities.Human.createBigInteger(in);
        System.out.println("bi.actual.tohex =" + actual.toString(16));
        System.out.println("bi.actual.tobics=" + 
                           BigIntUtilities.Checksum.createMd5CheckSumString(actual));
        Assert.assertEquals("test s=" + in, expected, actual);
    }
    
    private static boolean passesMillerRabin(BigInteger us,
                                             int iterations, 
                                             Random rnd) 
    {
        final BigInteger ONE = BigInteger.ONE;
        final BigInteger TWO = BigInteger.valueOf(2);
        // Find a and m such that m is odd and this == 1 + 2**a * m
        BigInteger thisMinusOne = us.subtract(ONE);
        BigInteger m = thisMinusOne;
        int a = m.getLowestSetBit();
        m = m.shiftRight(a);

        // Do the tests
        if (rnd == null) 
        {
            rnd = new SecureRandom();
        }
        for (int i=0; i<iterations; i++) 
        {
            // Generate a uniform random on (1, this)
            BigInteger b;
            do 
            {
                b = new BigInteger(us.bitLength(), rnd);
            } while (b.compareTo(ONE) <= 0 || b.compareTo(us) >= 0);

            int j = 0;
            BigInteger z = b.modPow(m, us);
            while(!((j==0 && z.equals(ONE)) || z.equals(thisMinusOne))) 
            {
                if (j>0 && z.equals(ONE) || ++j==a)
                    return false;
                z = z.modPow(TWO, us);
            }
        }
        return true;
    }
    
    
}

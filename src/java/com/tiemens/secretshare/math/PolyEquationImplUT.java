package com.tiemens.secretshare.math;

import java.math.BigInteger;

import junit.framework.Assert;
import junit.framework.TestCase;

public class PolyEquationImplUT
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
    public void testBasic()
    {
        // REMEMBER: arguements are REVERSED:
        PolyEquationImpl poly = PolyEquationImpl.create(432, 13, 5, 8);
        int x = 0;
        subtest(poly, x, BigInteger.valueOf((8*(x*x*x)) + (5*(x*x)) + (13*x) + 432));
        //subtest(poly, x, BigInteger.valueOf(432));
        x = 1;
        subtest(poly, x, BigInteger.valueOf((8*(x*x*x)) + (5*(x*x)) + (13*x) + 432));
        x = 2;
        subtest(poly, x, BigInteger.valueOf((8*(x*x*x)) + (5*(x*x)) + (13*x) + 432));

    }
    // ==================================================
    // non public methods
    // ==================================================

    private void subtest(PolyEquationImpl poly,
                         int x,
                         BigInteger expected)
    {
        BigInteger actual = poly.calculateFofX(BigInteger.valueOf(x));
        Assert.assertEquals("test x=" + x, expected, actual);
    }
}
package com.tiemens.secretshare.math;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tiemens.secretshare.math.EasyLinearEquation.EasySolve;

import junit.framework.Assert;
import junit.framework.TestCase;

public class EasyLinearEquationUT
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

    public void testFirst()
    {
        EasyLinearEquation ele = null;
        
        EasyLinearEquation
            .create(new int[][]
                   {
                    { 10, 83,   32,   23},
                    { 44,  5,   13,   22},
                    { 59,  31,  82,   99}
                   });
        
        ele = EasyLinearEquation
        .create(new int[][]
                          {
                           { 33, 1,   2,   3},
                           { 81, 4,   5,   6},
                           { 52, 3,   2,   4}
                          });
        
        EasySolve solve = ele.solve();
        Assert.assertEquals("1 should be 6", 
                            BigInteger.valueOf(6), solve.getAnswer(1));
        Assert.assertEquals("2 should be 3", 
                            BigInteger.valueOf(3), solve.getAnswer(2));
        Assert.assertEquals("3 should be 7", 
                            BigInteger.valueOf(7), solve.getAnswer(3));
    }
    
    public void testJavadocExample() 
        throws SecurityException, IOException
    {
        if (true)
        {
            // example code to turn on ALL logging 
            //
            // To see logging:
            // [a] set the handler's level
            // [b] add the handler
            // [c] set the logger's level
            
            Logger l = EasyLinearEquation.logger;
            Handler lh = new ConsoleHandler();
            // don't forget to do this:
            lh.setLevel(Level.ALL);
            
            // alternative: write log to file:
            //lh = new FileHandler("log.txt");
            
            // need this too:
            l.addHandler(lh);
            // and this:
            l.setLevel(Level.ALL);
            if (EasyLinearEquation.logger.isLoggable(Level.FINE))
            {
                System.out.println("ok");
                EasyLinearEquation.logger.fine("Hi there");
            }
            else
            {
                System.out.println("failed");
            }
        }
        
        EasyLinearEquation ele = null;
        
        ele = EasyLinearEquation
            .create(new int[][]
                   {
                    { 1491,  83,   32,   22},
                    { 329,    5,   13,   22},
                    { 122,    3,    2,   19}
                   });
        EasySolve solve = ele.solve();
        System.out.println("Output testJavadocExample test case.");
        System.out.println("answer(1)=" + solve.getAnswer(1));
        System.out.println("answer(2)=" + solve.getAnswer(2));
        System.out.println("answer(3)=" + solve.getAnswer(3));
    }
    
    /**
     * Taken from wikipedia example at Shamir's_Secret_Sharing.
     * This is a n=6, k=3 example, with a secret of '1234' 
     * f(x) = 1234 + 166x + 94x^2
     * 
     * f(1) = 1494   f(2) = 1942   f(3) = 2578   f(4) = 3402   f(5) = 4414   f(6) = 5614
     * The page selects secrets numbered: 2, 4 and 5
     */
    public void testFirstPolynomial()
    {
        EasyLinearEquation ele = null;
        
        BigInteger[] xarray = new BigInteger[] 
                                             {
                BigInteger.valueOf(2),
                BigInteger.valueOf(4),
                BigInteger.valueOf(5),
                                             };
        
        BigInteger[] fofxarray = new BigInteger[] 
                                             {
                BigInteger.valueOf(1942),
                BigInteger.valueOf(3402),
                BigInteger.valueOf(4414),
                                             };
        ele = EasyLinearEquation.createForPolynomial(xarray, fofxarray);
        
        EasySolve solve = ele.solve();
        Assert.assertEquals("1 should be 1234", 
                            BigInteger.valueOf(1234), solve.getAnswer(1));
        Assert.assertEquals("2 should be 166", 
                            BigInteger.valueOf(166), solve.getAnswer(2));
        Assert.assertEquals("3 should be 94", 
                            BigInteger.valueOf(94), solve.getAnswer(3));
    }
    // ==================================================
    // non public methods
    // ==================================================
   
    
}
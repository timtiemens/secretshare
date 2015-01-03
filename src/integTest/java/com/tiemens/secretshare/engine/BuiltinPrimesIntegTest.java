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
package com.tiemens.secretshare.engine;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.tiemens.secretshare.math.BigIntUtilitiesTest;
import com.tiemens.secretshare.math.EasyLinearEquationTest;

public class BuiltinPrimesIntegTest
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

    private int iterations = 100;


    //
    // mac-corei7 = macbook pro, corei7, 2.3GHz
    //

    /**
     * On the mac-corei7, this test takes
     *     1,000
     *    10,000    0.65 seconds
     */
    @Test
    public void testRabinMiller192()
    {
        BigInteger prime192 = SecretShare.getPrimeUsedFor192bitSecretPayload();
        subtest("192", prime192, iterations);
    }

    /**
     * On the mac-corei7, this test takes
     *     1,000
     *    10,000    2.32 seconds
     */
    @Test
    public void testRabinMiller384()
    {
        BigInteger prime384 = SecretShare.getPrimeUsedFor384bitSecretPayload();
        subtest("384", prime384, iterations);
    }

    /**
     * On the mac-corei7, this test takes
     *     1,000     125 seconds
     *    10,000    1261 seconds
     */
    @Test
    public void testRabinMiller4096()
    {
        BigInteger prime4096 = SecretShare.getPrimeUsedFor4096bigSecretPayload();
        subtest("4096", prime4096, iterations);
    }

    // ==================================================
    // non public methods
    // ==================================================


    private void subtest(String where, BigInteger prime, int iterations)
    {
        boolean b = BigIntUtilitiesTest.passesMillerRabin(prime, iterations, generateWithFixedSeed());
        Assert.assertTrue("Rabin-Miller (" + iterations + ") failed on " + where, b);
    }

    private Random generateWithFixedSeed()
    {
        // if anything does go wrong, we want to be able to reproduce it
        Random ret = new SecureRandom();
        ret.setSeed(1212L);
        return ret;
    }

    @SuppressWarnings("unused")
    private void enableAllLogging()
    {
        EasyLinearEquationTest.enableLogging();
        // add any other loggers here:
        //Logger l = Foo.logger;
        //l.addHandler(lh);
        //l.setLevel(Level.ALL);
    }

}

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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;


import com.tiemens.secretshare.math.equation.EasyLinearEquationTest;
import com.tiemens.secretshare.math.type.BigIntUtilitiesTest;

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
    // vm-amd = virtualbox, FX-8350 8 core, 4.0GHz
    // vm-corei7 = vmworkstation, corei7, 2.67GHz (original i7)
    // vm-xeon = vmworkstation, Xeon E5-2640@2.5GHz
    //

    /**
     * On the mac-corei7, this test takes
     *     1,000
     *    10,000    0.65 seconds
     * On the vm-corei7,
     *    100,000  15.1 seconds
     * On the vm-xeon,
     *     10,000   0.4 seconds
     *    100,000   3.0 seconds
     */
    @Test
    public void testRabinMiller192()
    {
        BigInteger prime192 = SecretShare.getPrimeUsedFor192bitSecretPayload();
        subtest("192", prime192, iterations);
    }

    /**
     * On the mac-corei7, this test takes
     *      1,000
     *     10,000   2.32 seconds
     * On the vm-corei7,
     *    100,000  74 seconds
     * On the vm-xeon,
     *     10,000   1.1 seconds
     *    100,000  10   seconds
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
     * On the vm-amd,
     *     1,000     203 seconds
     * On the vm-corei7,
     *     1,000     504 seconds
     *   100,000  50,627 seconds
     * On the vm-xeon,
     *       100
     *     1,000      49 seconds
     *    10,000     490 seconds
     *   100,000   4,894 seconds
     */
    @Test
    public void testRabinMiller4096()
    {
        BigInteger prime4096 = SecretShare.getPrimeUsedFor4096bigSecretPayload();
        subtest("4096", prime4096, iterations);
    }

    /**
     * On the vm-xeon,
     *      100       38 seconds
     *    1,000      369 seconds
     *   10,000    3,701 seconds
     *  100,000   36,907 seconds
     */
    @Test
    public void testRabinMiller8192()
    {
        BigInteger prime8192 = SecretShare.getPrimeUsedFor8192bigSecretPayload();
        subtest("8192", prime8192, iterations);
    }

    // ==================================================
    // non public methods
    // ==================================================


    private void subtest(String where, BigInteger prime, int iterations)
    {
        boolean b = BigIntUtilitiesTest.passesMillerRabin(prime, iterations, generateWithFixedSeed());
        assertTrue(b, "Rabin-Miller (" + iterations + ") failed on " + where);
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

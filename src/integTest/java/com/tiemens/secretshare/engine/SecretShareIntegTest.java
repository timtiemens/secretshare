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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import com.tiemens.secretshare.math.BigIntUtilities;
import com.tiemens.secretshare.math.EasyLinearEquationTest;

public class SecretShareIntegTest
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
     * This test takes 65 to 80 seconds to run 1000 trials.
     * corei7 down to 20 seconds.
     */
    @Test
    public void testMassiveLoop384()
    {
        subtestMassiveLoop(SecretShare.getPrimeUsedFor384bitSecretPayload());
    }

    /**
     * This test takes 65 to 80 seconds to run 1000 trials.
     * corei7 down to 20 seconds.
     */
    @Test
    public void testMassiveLoop4096()
    {
        subtestMassiveLoop(SecretShare.getPrimeUsedFor4096bigSecretPayload());
    }


    private void subtestMassiveLoop(final BigInteger prime)
    {
        final int n = 9;
        final int k = 4;

        SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n, k, prime, "massive 384");

        SecretShare secretShare = new SecretShare(publicInfo);
        Random random = new Random(1234L);
        int trials = 1000;
        for (int i = 0; i < trials; i++)
        {
            System.out.println("Trial#" + i);

            //                   1         2         3         4
            //          1234567890123456789012345678901234567890123456
            String s = "This is a 45 character secret string as input";
            BigInteger secret = BigIntUtilities.Human.createBigInteger(s);

            if (secret.signum() <= 0)
            {
                Assert.fail("Secret cannot be negative");
            }
            SecretShare.SplitSecretOutput generate = secretShare.split(secret, random);

            System.out.println("Trial#" + i + " secret=" + secret);
            System.out.println(generate.debugDump());

            BigInteger reconstructed = subtestReconstruction(generate.getShareInfos());

            Assert.assertEquals("Secrets do not match", secret, reconstructed);
        }
    }



    /**
     * corei7 27 seconds.
     */
    @Test
    public void testBig192()
    {
        // This one used to fail:
        subtestBigBig(SecretShare.getPrimeUsedFor192bitSecretPayload(),
                      new BigInteger("12345678998765432100112233445566778899"));

        // add a '1' after 100 and it works:
        subtestBigBig(SecretShare.getPrimeUsedFor192bitSecretPayload(),
                      new BigInteger("123456789987654321001112233445566778899"));
    }

    /**
     * corei7 15 seconds.
     */
    @Test
    public void testBig384()
    {
        subtestBigBig(SecretShare.getPrimeUsedFor384bitSecretPayload(),
                      new BigInteger("12345678998765432100112233445566778899" +
                                     "000000000012345678987654321"
                                     ));
    }

    public void subtestBigBig(final BigInteger prime,
                              final BigInteger secret)
    {
        subtestBigBig2(null, secret);   // test with NO modulus
        subtestBigBig2(prime, secret);  // test with modulus
    }

    public void subtestBigBig2(final BigInteger prime,
                               final BigInteger secret)
    {
        final int n = 16;
        final int k = 8;

        System.out.println("Generating shares...");
        SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n, k, prime, "test big big");

        SecretShare secretShare = new SecretShare(publicInfo);
        Random random = new Random(1234L);
        SecretShare.SplitSecretOutput generate = secretShare.split(secret, random);
        System.out.println(generate.debugDump());


        System.out.println("Solving using shares...");

        BigInteger reconstructed = subtestReconstruction(generate.getShareInfos());

        Assert.assertEquals("Secrets do not match", secret, reconstructed);
    }

    /**
     * Notice the signature of this method:  all this method gets are the ShareInfo objects,
     *    and it returns the (secret) BigInteger
     *
     * @param shares of the secret
     * @return secret
     */
    private BigInteger subtestReconstruction(List<SecretShare.ShareInfo> shares)
    {
        if (true)
        {
            subtestAllCombinations(shares);
        }

        // pick the first share's public info:
        SecretShare.PublicInfo publicInfo = shares.get(0).getPublicInfo();

        // create a new solver from just the public info:
        SecretShare solver = new SecretShare(publicInfo);


        // pick some of the shares
        List<SecretShare.ShareInfo> usetheseshares =
            new ArrayList<SecretShare.ShareInfo>();
        for (int i = 0, max = publicInfo.getK(); i < max; i++)
        {
            usetheseshares.add(shares.get(i));
        }

        // enableAllLogging();

        SecretShare.CombineOutput solved = solver.combine(usetheseshares);
        System.out.println("Reconstructed secret=" + solved.getSecret());

        return solved.getSecret();

    }

    private void subtestAllCombinations(List<ShareInfo> shares)
    {
        // enableAllLogging();

        // pick the first share's public info:
        SecretShare.PublicInfo publicInfo = shares.get(0).getPublicInfo();

        // create a new solver from just the public info:
        SecretShare solver = new SecretShare(publicInfo);

        BigInteger secret = solver.combineParanoid(shares).getAgreedAnswer();
        Assert.assertNotNull(secret);
    }


    /**
     * corei7 112 seconds.
     */
    @Test
    public void testUntilItFails()
    {
        subtestUntilItFails("192", 192, SecretShare.getPrimeUsedFor192bitSecretPayload());
        subtestUntilItFails("384", 384, SecretShare.getPrimeUsedFor384bitSecretPayload());
        subtestUntilItFails("4096", 4096, SecretShare.getPrimeUsedFor4096bigSecretPayload());
    }


    private void subtestUntilItFails(String which, final int maxbits, BigInteger modulus)
    {
        int startNumBits = modulus.bitLength() - 15;
        boolean ok = false;
        for (int bits = startNumBits; bits <= maxbits; bits++)
        {
            for (int round = 0; round < 300; round++)
            {
                ok = subtestThisModulusThisSizeSecret(modulus, bits, round);
                if (! ok)
                {
                    System.out.println(which + " failed at secret size bits=" + bits);
                    Assert.fail("just too much output");
                    break;
                }
            }
        }
        if (ok)
        {
            System.out.println(which + " worked for everything up to bits=" + maxbits);
        }
    }

    private boolean subtestThisModulusThisSizeSecret(final BigInteger prime,
                                                     final int bits,
                                                     final int round)
    {
        Random random;
        random = new Random(1234L);
        BigInteger base =     BigInteger.valueOf(2L).pow(bits);
        BigInteger addto =    BigInteger.probablePrime(155, new Random());
        BigInteger evenmore = BigInteger.probablePrime(100, new Random()).multiply(BigInteger.valueOf(round));
        BigInteger secret = base.add(addto.add(evenmore));

        final int n = 6;
        final int k = 6;

        SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n, k, prime, "run until fail test");
        SecretShare secretShare = new SecretShare(publicInfo);

        random = new Random(1234L);
        SecretShare.SplitSecretOutput generate = secretShare.split(secret, random);

        BigInteger reconstructed = subtestReconstruction(generate.getShareInfos());

        return secret.equals(reconstructed);
    }

    /**
     * corei7 37 seconds.
     */
    @Test
    public void testCreateRandomModulus()
    {
        int n = 100;
        int bits = 150;

        for (int i = 0; i < n; i++)
        {
            BigInteger base = BigInteger.valueOf(2L).pow(bits);
            BigInteger add = BigInteger.probablePrime(50, new Random());
            BigInteger secret = base.add(add);

            BigInteger mod = SecretShare.createRandomModulusForSecret(secret);
            Assert.assertTrue("modulus " + mod + " is incorrect for secret " + secret,
                              (mod.compareTo(secret) > 0));

            bits += 10;
            System.out.print(".");
        }
        System.out.println("");
    }


    // ==================================================
    // non public methods
    // ==================================================


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

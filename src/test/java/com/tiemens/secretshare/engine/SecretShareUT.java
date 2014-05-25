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
import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntStringChecksum;
import com.tiemens.secretshare.math.EasyLinearEquationUT;

public class SecretShareUT
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

    // @Before


    // ==================================================
    // public methods
    // ==================================================

    @Test
    public void testStandard192prime()
    {
        BigInteger p = SecretShare.getPrimeUsedFor192bitSecretPayload();
        Assert.assertNotNull(p);
    }

    @Test
    public void testFirst()
    {
        final int n = 6;
        final int k = 3;
        BigInteger prime = null;

        prime = BigInteger.valueOf(59561);
        SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n, k, prime, "test first");
        SecretShare secretShare = new SecretShare(publicInfo);
        final BigInteger secret = BigInteger.valueOf(45654L);
        Random random = new Random(1234L);
        SecretShare.SplitSecretOutput generate = secretShare.split(secret, random);

        System.out.println("TestFirst, secret=" + secret);
        System.out.println(generate.debugDump());


        System.out.println("Solving using shares...");

        BigInteger reconstructed = subtestReconstruction(generate.getShareInfos());

        Assert.assertEquals("Secrets do not match", secret, reconstructed);

        subtestDuplicateSharesReconstruction(generate.getShareInfos());
    }


    private void subtestDuplicateSharesReconstruction(List<SecretShare.ShareInfo> shares)
    {
        // pick the first share's public info:
        SecretShare.PublicInfo publicInfo = shares.get(0).getPublicInfo();

        // create a new solver from just the public info:
        SecretShare solver = new SecretShare(publicInfo);

        // pick ONE TOO FEW of the shares,
        List<SecretShare.ShareInfo> usetheseshares =
            new ArrayList<SecretShare.ShareInfo>();
        for (int i = 0, max = publicInfo.getK() - 1 ; i < max; i++)
        {
            usetheseshares.add(shares.get(i));
        }

        try
        {
            SecretShare.CombineOutput solved = solver.combine(usetheseshares);
            Assert.fail("Failed too few secrets test. solved=" + solved);
        }
        catch (SecretShareException e)
        {
            System.out.println("Passed Too Few secrets test.");
        }

        // Now add #0 again [duplicate]
        usetheseshares.add(shares.get(0));
        try
        {
            SecretShare.CombineOutput solved = solver.combine(usetheseshares);
            Assert.fail("Failed duplicate secrets test. solved=" + solved);
        }
        catch (SecretShareException e)
        {
            System.out.println("Passed duplicate secrets test.");
        }

        // TODO: test shares that have different publicInfo objects
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



    @Test
    public void testPrint4096bigint()
    {
        BigInteger b;
        //b = BigInteger.valueOf(2L).pow(4100).nextProbablePrime();
        b = SecretShare.getPrimeUsedFor4096bigSecretPayload();
        System.out.println(b);
        String bics = BigIntStringChecksum.create(b).toString();
        System.out.println(bics);
    }



    @Test
    public void testCreateRandomModulusForSecret() {
        Random random = new Random(1234L);

        String[] secret4expected = new String [] {
                "500", "757",
                "250000", "1671671",
                "125000000", "4248385987",
                "750000000000000", "1120190824028631461",
        };

        for (int i = 0, n = secret4expected.length; i < n; i += 2)
        {
            BigInteger secret = new BigInteger(secret4expected[i + 0]);
            BigInteger expected = new BigInteger(secret4expected[i + 1]);

            BigInteger actual = SecretShare.createRandomModulusForSecret(secret, random);
            Assert.assertEquals("secret=" + secret, expected, actual);
            Assert.assertTrue("not bigger than", actual.compareTo(secret) > 0);
            Assert.assertTrue("isTheModulusAppropriateForSecret", SecretShare.isTheModulusAppropriateForSecret(actual, secret));
        }
    }

    @Test
    public void testIsTheModulusAppropriateForSecret()
    {
        BigInteger secret = new BigInteger("100");
        Assert.assertFalse(SecretShare.isTheModulusAppropriateForSecret(new BigInteger("99"), secret));
        Assert.assertFalse(SecretShare.isTheModulusAppropriateForSecret(new BigInteger("100"), secret));
        Assert.assertTrue(SecretShare.isTheModulusAppropriateForSecret(new BigInteger("101"), secret));
    }


    @Test
    public void testPayloadModulus()
    {
        BigInteger p4096 = SecretShare.getPrimeUsedFor4096bigSecretPayload();
        BigInteger p384 = SecretShare.getPrimeUsedFor384bitSecretPayload();
        BigInteger p192 = SecretShare.getPrimeUsedFor192bitSecretPayload();

        Assert.assertNotNull(p4096);
        Assert.assertNotNull(p384);
        Assert.assertNotNull(p192);
    }

    // ==================================================
    // non public methods
    // ==================================================

    @SuppressWarnings("unused")
    private void enableAllLogging()
    {
        EasyLinearEquationUT.enableLogging();
        // add any other loggers here:
        //Logger l = Foo.logger;
        //l.addHandler(lh);
        //l.setLevel(Level.ALL);

    }


}

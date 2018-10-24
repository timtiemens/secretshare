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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntStringChecksum;
import com.tiemens.secretshare.math.EasyLinearEquationTest;

public class SecretShareTest
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
        final int n = 16;
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
        for (int i = 0, max = publicInfo.getK() - 1; i < max; i++)
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
    public void testPrint8192bigint()
    {
        BigInteger b;
        // takes 15.5 seconds pow(8200).nextProbablePrime()
        //b = BigInteger.valueOf(2L).pow(8200).nextProbablePrime();
        b = SecretShare.getPrimeUsedFor8192bigSecretPayload();
        String bs = "" + b;
        System.out.println("#Digits=" + bs.length());
        System.out.println("Prime(8200)=");
        System.out.println(b);
        String bics = BigIntStringChecksum.create(b).toString();
        System.out.println(bics);
    }


    @Test
    public void testCreateRandomModulusForSecret()
    {
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
            Assert.assertTrue("isTheModulusAppropriateForSecret",
                              SecretShare.isTheModulusAppropriateForSecret(actual, secret));
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

    private BigInteger bi(int a)
    {
        return new BigInteger("" + a);
    }

    @Test
    public void testCanDetectMismatchPublicInfo()
    {
        int n = 6, k = 3;
        BigInteger modulus = new BigInteger("57");
        SecretShare.PublicInfo publicInfo =
                new SecretShare.PublicInfo(n, k, modulus, "correct baseline");
        SecretShare.PublicInfo publicInfoMissingN =
                new SecretShare.PublicInfo(null, k, modulus, "correct baseline");

        SecretShare.PublicInfo piWrongN =
                new SecretShare.PublicInfo(n + 1, k, modulus, "wrong n");
        SecretShare.PublicInfo piWrongKneg =
                new SecretShare.PublicInfo(n, k - 1, modulus, "wrong k=k-1");
        SecretShare.PublicInfo piWrongKpos =
                new SecretShare.PublicInfo(n, k + 1, modulus, "wrong k=k+1");
        SecretShare.PublicInfo piWrongModulus =
                new SecretShare.PublicInfo(n, k, modulus.add(BigInteger.TEN), "wrong modulus");


        // jar split -n 6 -k 3 -sN 50 -m 57
        // x(1) = 20   x(2) =  2   x(3) = 53   x(4) = 2    x(5) = 20
        final BigInteger secret = bi(50);
        SecretShare.ShareInfo s1 = new SecretShare.ShareInfo(1, bi(20), publicInfo);
        SecretShare.ShareInfo s2 = new SecretShare.ShareInfo(2, bi(2), publicInfo);
        SecretShare.ShareInfo s3 = new SecretShare.ShareInfo(3, bi(53), publicInfo);

        List<SecretShare.ShareInfo> shares;

        // double-check can solve:
        shares = Arrays.asList(s1, s2, s3);
        SecretShare secretShare = new SecretShare(publicInfo);
        SecretShare.CombineOutput combine = secretShare.combine(shares);
        Assert.assertEquals("baseline failed", secret, combine.getSecret());
        assertOk("baseline failed 2", secret, publicInfo, shares);
        //assertThrows("baseline failed 2", secret, publicInfo, shares);

        // can not test: all shareInfo.publicInfo are null,
        //   because constructor prevents null public info:
        //     no:new SecretShare.ShareInfo(2, bi(20), null),

        //
        // SHOULD BE OK
        //

        // no public info has "n"
        shares = Arrays.asList(new SecretShare.ShareInfo(1, bi(20), publicInfoMissingN),
                               new SecretShare.ShareInfo(2, bi(2), publicInfoMissingN),
                               new SecretShare.ShareInfo(3, bi(53), publicInfoMissingN));
        assertOk("all missing n", secret, publicInfoMissingN, shares);

        //
        // SHOULD FAIL
        //

        // "outer" public info has wrong "k"
        assertThrows("outer pi kneg", secret, piWrongKneg, shares);
        assertThrows("outer pi kpos", secret, piWrongKpos, shares);


        // "list[1]" public info has wrong "n"
        shares = Arrays.asList(s1,
                               new SecretShare.ShareInfo(2, bi(2), piWrongN),
                               s3);
        assertThrows("share[1] pi n wrong", secret, publicInfo, shares);
        assertThrows("share[1] pi n wrong and outer n wrong", secret, piWrongN, shares);

        // "list[2] public info has wrong "k"
        shares = Arrays.asList(s1,
                               s2,
                               new SecretShare.ShareInfo(3, bi(53), piWrongKpos));
        assertThrows("share[2] pi k wrong", secret, publicInfo, shares);

        // "list[0] public info has wrong "modulus"
        shares = Arrays.asList(new SecretShare.ShareInfo(1, bi(20), piWrongModulus),
                               s2,
                               s3);
        assertThrows("share[0] pi modulus wrong", secret, publicInfo, shares);
    }

    private void assertThrows(String where, BigInteger secret,
                              SecretShare.PublicInfo publicInfo,
                              List<SecretShare.ShareInfo> shares)
    {
        SecretShare secretShare = new SecretShare(publicInfo);
        try
        {
            SecretShare.CombineOutput combine = secretShare.combine(shares);
            Assert.fail(where + ": should have thrown exception, but did not, instead returned=" + combine);
        }
        catch (SecretShareException sse)
        {
            // ok
        }

    }

    private void assertOk(String where, BigInteger secret,
                          SecretShare.PublicInfo publicInfo,
                          List<SecretShare.ShareInfo> shares)
    {
        SecretShare secretShare = new SecretShare(publicInfo);
        SecretShare.CombineOutput combine = secretShare.combine(shares);
        Assert.assertEquals(where, secret, combine.getSecret());
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

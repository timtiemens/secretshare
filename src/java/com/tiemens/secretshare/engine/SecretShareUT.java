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
package com.tiemens.secretshare.engine;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntUtilities;
import com.tiemens.secretshare.math.BigIntUtilitiesUT;
import com.tiemens.secretshare.math.EasyLinearEquationUT;

public class SecretShareUT
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
    public void testStandard192prime()
    {
        BigInteger p = SecretShare.getPrimeUsedFor192bitSecretPayload();
        Assert.assertNotNull(p);
    }
    
    public void testFirst()
    {
        final int n = 6;
        final int k = 3;
        BigInteger prime = null;
        
        // later:
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

    /**
     * This test takes 65 to 80 seconds to run 1000 trials. 
     */
    public void testMassiveLoop384()
    {
        final int n = 9;
        final int k = 4;
        final BigInteger prime = SecretShare.getPrimeUsedFor384bitSecretPayload();
        
        SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n, k, prime, "massive 384");
        
        SecretShare secretShare = new SecretShare(publicInfo);
        Random random = new Random(1234L);
        int trials = 1000;
        for (int i = 0; i < trials; i++)
        {
            System.out.println("Trial#" + i);
            BigInteger secret;
            if (false)
            {
                byte[] bytes = new byte[25];
                random.nextBytes(bytes);
                secret = new BigInteger(bytes);
                if (secret.signum() <= 0)
                {
                    secret = secret.negate();
                }
            }
            else
            {
                //                   1         2         3         4   
                //          1234567890123456789012345678901234567890123456
                String s = "This is a 45 character secret string as input";
                secret = BigIntUtilities.createFromHumanStringBytes(s);
            }
            
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
    
    
    
    private void subtestDuplicateSharesReconstruction(List<SecretShare.ShareInfo> shares)
    {
        // pick the first share's public info:
        SecretShare.PublicInfo publicInfo = shares.get(0).getPublicInfo();
            
        // create a new solver from just the public info:
        SecretShare solver = new SecretShare(publicInfo);
            
        //     
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

    public void testBig192()
    {
        // This one used to fail:
        subtestBigBig(SecretShare.getPrimeUsedFor192bitSecretPayload(),
                      new BigInteger("12345678998765432100112233445566778899"));
        
        // add a '1' after 100 and it works:
        subtestBigBig(SecretShare.getPrimeUsedFor192bitSecretPayload(),
                      new BigInteger("123456789987654321001112233445566778899"));
    }
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
        if (false)
        {
            enableAllLogging();
        }
        SecretShare.CombineOutput solved = solver.combine(usetheseshares);
        System.out.println("Reconstructed secret=" + solved.getSecret());
     
        return solved.getSecret();

    }

    private void subtestAllCombinations(List<ShareInfo> shares)
    {
        if (false)
        {
            enableAllLogging();
        }
        // pick the first share's public info:
        SecretShare.PublicInfo publicInfo = shares.get(0).getPublicInfo();
        
        // create a new solver from just the public info:
        SecretShare solver = new SecretShare(publicInfo);
        
        BigInteger secret = solver.combineParanoid(shares);
        Assert.assertNotNull(secret);
    }
        

    private void enableAllLogging()
    {
        EasyLinearEquationUT.enableLogging();
        // add any other loggers here:
        //Logger l = Foo.logger;
        //l.addHandler(lh);
        //l.setLevel(Level.ALL);        
        
    }
 
    // ==================================================
    // non public methods
    // ==================================================
    
    
}

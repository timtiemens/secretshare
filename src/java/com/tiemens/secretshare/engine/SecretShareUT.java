package com.tiemens.secretshare.engine;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.EasyLinearEquation;
import com.tiemens.secretshare.math.EasyLinearEquationUT;

import junit.framework.Assert;
import junit.framework.TestCase;

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
        SecretShare.GenerateSharesOutput generate = secretShare.generate(secret, random);
        System.out.println(generate.debugDump());
        
        
        System.out.println("Solving using shares/shards...");
        
        BigInteger reconstructed = subtestReconstruction(generate.getSecretShares());
        
        Assert.assertEquals("Secrets do not match", secret, reconstructed);
    }
    
    
    public void testBigBig()
    {
        final int n = 16;
        final int k = 8;
        BigInteger prime = null;
        
        prime = SecretShare.getPrimeUsedFor192bitSecretPayload();
        
        System.out.println("Generating shares/shards...");
        SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n, k, prime, "test big big");
        SecretShare secretShare = new SecretShare(publicInfo);
        final BigInteger secret = new BigInteger("12345678998765432100112233445566778899");
        Random random = new Random(1234L);
        SecretShare.GenerateSharesOutput generate = secretShare.generate(secret, random);
        System.out.println(generate.debugDump());
        
        
        System.out.println("Solving using shares/shards...");
        
        BigInteger reconstructed = subtestReconstruction(generate.getSecretShares());
        
        Assert.assertEquals("Secrets do not match", secret, reconstructed);
    }
    /**
     * Notice the signatures:  all this routine gets are the ShareInfo objects,
     *    and it returns the (secret) BigInteger
     *    
     * @param shares shards of the secret
     * @return secret
     */
    private BigInteger subtestReconstruction(List<SecretShare.ShareInfo> shares)
    {
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
        SecretShare.SolveOutput solved = solver.solve(usetheseshares);
        System.out.println("Reconstructed secret=" + solved.getSecret());
     
        return solved.getSecret();

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
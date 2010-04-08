package com.tiemens.secretshare.engine;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntStringChecksum;
import com.tiemens.secretshare.math.EasyLinearEquation;
import com.tiemens.secretshare.math.PolyEquationImpl;

/**
 * Main class for the "Shamir's Secret Sharing" implementation.
 * 
 * General description: 
 *   A secret is divided into "n" pieces of data,
 *   such that "k" of those pieces can be used to reconstruct the secret,
 *   but "k-1" of those pieces gets you nothing.
 *   
 * The polynomials generated will be order "k-1", 
 *   e.g. if k=3, then f(x) = secret + a*x^1 + b*x^2
 *    and there will be "k" of those polynomials, 
 *    each with random [and discarded] coefficients 'a', 'b', etc.
 *      
 * @author tiemens
 *
 */
public class SecretShare
{
    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================
    
    public static BigInteger getPrimeUsedFor192bitSecretPayload()
    {
        // This big integer was created with probablePrime(194-bits)
        // This prime has been tested via http://www.alpertron.com.ar/ECM.HTM
        // This prime has been tested with 100,000 iterations of Miller-Rabin
        // This prime is bigger than 2^192
        BigInteger p194one = 
            new BigInteger("14976407493557531125525728362448106789840013430353915016137");
        
        String bigintcs = 
            "bigintcs:000002-62c8fd-6ec81b-3c0584-136789-80ad34-9269af-da237f-8ff3c9-12BCCD";
        BigInteger p194two = 
            BigIntStringChecksum.fromString(bigintcs).asBigInteger();
        
        if (p194one.equals(p194two))
        {
            return p194one;
        }
        else
        {
            throw new SecretShareException("192-bit prime failure");
        }
    }
    // ==================================================
    // instance data
    // ==================================================
    private final PublicInfo publicInfo;

    
    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================
    public SecretShare(final PublicInfo inPublicInfo)
    {
        publicInfo = inPublicInfo;
    }
    public GenerateSharesOutput generate(final BigInteger secret)
    {
        return generate(secret, new SecureRandom());
    }
    public GenerateSharesOutput generate(final BigInteger secret,
                                         final Random random)
    {
        BigInteger[] coeffs = new BigInteger[publicInfo.getK()];

        // create the equation by setting the coefficients:
        // [a] randomize the coefficients:
        randomizeCoeffs(coeffs, random);
        // [b] set the constant coefficient to the secret:
        coeffs[0] = secret;
        
        final PolyEquationImpl equation = new PolyEquationImpl(coeffs);

        GenerateSharesOutput ret = new GenerateSharesOutput(this.publicInfo,
                                                            equation);
        
        for (int x = 1, n = publicInfo.getN() + 1; x < n; x++)
        {
            final BigInteger fofx = equation.calculateFofX(BigInteger.valueOf(x));
            BigInteger data = fofx;
            if (publicInfo.primeModulus != null)
            {
                data = data.mod(publicInfo.primeModulus);
            }
            final ShareInfo share = new ShareInfo(x, data, this.publicInfo);
            ret.sharesInfo.add(share);
        }
        
        return ret;
    }

    public SolveOutput solve(final List<ShareInfo> usetheseshares)
    {
        SolveOutput ret = null;
        
        if (publicInfo.getK() > usetheseshares.size())
        {
            throw new SecretShareException("Must have " + publicInfo.getK() +
                                           " shares to solve.  Only provided " + 
                                           usetheseshares.size());
        }
        final int size = publicInfo.getK();
        BigInteger[] xarray = new BigInteger[size];
        BigInteger[] fofxarray = new BigInteger[size];
        for (int i = 0, n = size; i < n; i++)
        {
            xarray[i] = usetheseshares.get(i).getXasBigInteger();
            fofxarray[i] = usetheseshares.get(i).getShare();
        }
        EasyLinearEquation ele = 
            EasyLinearEquation.createForPolynomial(xarray, fofxarray);
        if (publicInfo.getPrimeModulus() != null)
        {
            ele = ele.createWithPrimeModulus(publicInfo.getPrimeModulus());
        }
        EasyLinearEquation.EasySolve solve = ele.solve();
        
        ret = new SolveOutput(solve.getAnswer(1));
        
        return ret;
    }

    // ==================================================
    // public methods
    // ==================================================

    private void randomizeCoeffs(BigInteger[] coeffs,
                                 Random random)
    {
        for (int i = 1, n = coeffs.length; i < n; i++)
        {
            BigInteger big = null;
            //big = BigInteger.valueOf((random.nextInt() % 20) + 1);
            // TODO: maybe bigger than long?
            big = BigInteger.valueOf(random.nextLong());
            // TODO: make it even bigger?
            //big = big.multiply(BigInteger.valueOf(random.nextLong()));
            coeffs[i] = big;
        }
    }

    public static class PublicInfo
    {
        // the required public info: "K" and the modulus
        private final int k;                         // gives the order of the polynomial
        private final BigInteger primeModulus;       // can be null

        // useful information: "N" - how many shares/shards were generated?
        private final int n;
        
        // just descriptive info:
        private final String description;   //
        private final String uuid;          // 
        
        
        public PublicInfo(final int inN,
                          final int inK,
                          final BigInteger inPrimeModulus,
                          final String inDescription)
        {
            super();
            this.n = inN;
            this.k = inK;
            this.primeModulus = inPrimeModulus;
            this.description = inDescription;
            
            UUID uuidobj = UUID.randomUUID();
            uuid =  uuidobj.toString();
         
            
            if (k > n)
            {
                throw new SecretShareException("k cannot be bigger than n [k=" + k + 
                                               " n=" + n + "]");
            }
            // enhancement: allow the modulus to be null:
            //if (inPrimeModulus == null)
            //{
            //    throw new SecretShareException("prime modulus cannot be null");
            //}
        }
        @Override
        public String toString()
        {
            return "PublicInfo[k=" + k + ", n=" + n + "\n" +
                "modulus=" + primeModulus + "\n" +
                "description=" + description + "\n" +
                "uuid=" + uuid +
                "]";
        }
        public String debugDump()
        {
            return toString();
        }
        public int getN()
        {
            return n;
        }
        public int getK()
        {
            return k;
        }
        public BigInteger getPrimeModulus()
        {
            return primeModulus;
        }
    }
    
    /**
     * Holds all the info needed to be a "piece" of the secret.
     * aka a "Share" of the secret.
     * 
     * @author tiemens
     *
     */
    public static class ShareInfo
    {
        private final int x;              // this is aka "the index"
        private final BigInteger share;   // our piece of the secret
        private final PublicInfo publicInfo;
        
        public ShareInfo(final int inX,
                         final BigInteger inShare,
                         final PublicInfo inPublicInfo)
        {
            x = inX;
            share = inShare;
            publicInfo = inPublicInfo;
        }
        public String debugDump()
        {
            return "ShareInfo[x=" + x + "\n" +
              "share=" + share +
              " public=" + publicInfo.debugDump() +
              "]";
        }
        public int getIndex()
        {
            return x;
        }
        public int getX()
        {
            return x;
        }
        public BigInteger getXasBigInteger()
        {
            return BigInteger.valueOf(x);
        }
        public BigInteger getShare()
        {
            return share;
        }
        public PublicInfo getPublicInfo()
        {
            return publicInfo;
        }
    }
    
    public static class GenerateSharesOutput
    {
        private final PublicInfo publicInfo;
        private final List<ShareInfo> sharesInfo = new ArrayList<ShareInfo>();
        private final PolyEquationImpl polynomial;
        
        public GenerateSharesOutput(final PublicInfo inPublicInfo,
                                    final PolyEquationImpl inPolynomial)
        {
            publicInfo = inPublicInfo;
            polynomial = inPolynomial;
        }
        public String debugDump()
        {
            String ret = "Public=" + publicInfo.debugDump() + "\n";
            
            ret += "EQ: " + polynomial.debugDump() + "\n";

            for (ShareInfo share : sharesInfo)
            {
                ret += "SHARE: " + share.debugDump() + "\n";
            }
            return ret;
        }
        public List<ShareInfo> getSecretShares()
        {
            return Collections.unmodifiableList(sharesInfo);
        }
    }
    
    public static class SolveOutput
    {
        private final BigInteger secret;
        public SolveOutput(final BigInteger inSecret)
        {
            secret = inSecret;
        }
        public BigInteger getSecret()
        {
            return secret;
        }
        
    }

    
    // ==================================================
    // non public methods
    // ==================================================
}
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

import java.io.PrintStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntStringChecksum;
import com.tiemens.secretshare.math.CombinationGenerator;
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

    // Both primes were tested via http://www.alpertron.com.ar/ECM.HTM
    // Both primes were tested with 100,000 iterations of Miller-Rabin

    public static BigInteger getPrimeUsedFor384bitSecretPayload()
    {
        // This big integer was created with probablePrime(386-bits)
        // This prime is bigger than 2^384
        BigInteger p194one = 
            new BigInteger("830856716641269388050926147210" +
                           "378437007763661599988974204336" +
                           "741171904442622602400099072063" +
                           "84693584652377753448639527");
        
        String bigintcs = 
            "bigintcs:000002-1bd189-52959f-874f79-3d6cf5-11ac82-e6cea4-46c19c-5f523a-5318c7-" +
            "e0f379-66f9e1-308c61-2d8d0b-dba253-6f54b0-ec6c27-3198DB";
        
        return checkAndReturn("384bit prime", p194one, bigintcs);
    }
    
    public static BigInteger getPrimeUsedFor192bitSecretPayload()
    {
        // This big integer was created with probablePrime(194-bits)
        // This prime is bigger than 2^192
        BigInteger p194one = 
            new BigInteger("14976407493557531125525728362448106789840013430353915016137");
        
        String bigintcs = 
            "bigintcs:000002-62c8fd-6ec81b-3c0584-136789-80ad34-9269af-da237f-8ff3c9-12BCCD";
        
        return checkAndReturn("192bit prime", p194one, bigintcs);
    }
    
    private static BigInteger checkAndReturn(String which,
                                             BigInteger expected,
                                             String asbigintcs)
    {
        BigInteger other = 
            BigIntStringChecksum.fromString(asbigintcs).asBigInteger();
        
        if (expected.equals(other))
        {
            return expected;
        }
        else
        {
            throw new SecretShareException(which + " failure");
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
    
    
    // ==================================================
    // public methods
    // ==================================================
    
    /**
     * Split the secret into pieces.
     * 
     * @param secret to split
     * @return split secret output instance
     */
    public SplitSecretOutput split(final BigInteger secret)
    {
        return split(secret, new SecureRandom());
    }
    
    /**
     * Split the secret into pieces, where the caller controls the random instance.
     * 
     * @param secret to split
     * @param random to use for random number generation
     * @return split secret output instance
     */
    public SplitSecretOutput split(final BigInteger secret,
                                   final Random random)
    {
        if (secret == null)
        {
            throw new SecretShareException("Secret cannot be null");
        }
        if (secret.signum() <= 0)
        {
            throw new SecretShareException("Secret cannot be negative");
        }
        if (publicInfo.getPrimeModulus() != null)
        {
            if (secret.compareTo(publicInfo.getPrimeModulus()) >= 0)
            {
                throw new SecretShareException("Secret cannot be larger than modulus.  " +
                                               "Secret=" + secret + "\n" +
                                               "Modulus=" + publicInfo.getPrimeModulus());
            }
        }
        
        BigInteger[] coeffs = new BigInteger[publicInfo.getK()];

        // create the equation by setting the coefficients:
        // [a] randomize the coefficients:
        randomizeCoeffs(coeffs, random, publicInfo.getPrimeModulus(), secret);
        // [b] set the constant coefficient to the secret:
        coeffs[0] = secret;
        
        final PolyEquationImpl equation = new PolyEquationImpl(coeffs);

        SplitSecretOutput ret = new SplitSecretOutput(this.publicInfo,
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

    /**
     * Combine the shares generated by the split to recover the secret.
     * 
     * @param usetheseshares shares to use
     * @return the combine output instance [which in turn contains the recovered secret]
     */
    public CombineOutput combine(final List<ShareInfo> usetheseshares)
    {
        CombineOutput ret = null;
        
        if (publicInfo.getK() > usetheseshares.size())
        {
            throw new SecretShareException("Must have " + publicInfo.getK() +
                                           " shares to solve.  Only provided " + 
                                           usetheseshares.size());
        }
        
        checkForDuplicatesOrThrow(usetheseshares);
        
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
        
        BigInteger solveSecret = solve.getAnswer(1);
        if (publicInfo.getPrimeModulus() != null)
        {
            solveSecret = solveSecret.mod(publicInfo.getPrimeModulus());
        }
        ret = new CombineOutput(solveSecret);
        
        
        return ret;
    }

    private void checkForDuplicatesOrThrow(List<ShareInfo> shares)
    {
        Set<ShareInfo> seen = new HashSet<ShareInfo>();
        for (ShareInfo s : shares)
        {
            if (seen.contains(s))
            {
                throw new SecretShareException("Duplicate share of " + s.debugDump());
            }
            else
            {
                seen.add(s);
            }
        }
        
    }

    // ==================================================
    // private methods
    // ==================================================

    private void randomizeCoeffs(final BigInteger[] coeffs,
                                 final Random random,
                                 final BigInteger modulus,
                                 final BigInteger secret)
    {
        for (int i = 1, n = coeffs.length; i < n; i++)
        {
            BigInteger big = null;
            //big = BigInteger.valueOf((random.nextInt() % 20) + 1);

            big = BigInteger.valueOf(random.nextLong());
            // ENHANCEMENT: provide better control?  make it even bigger?
            // for now, we'll just do long^2:
            big = big.multiply(BigInteger.valueOf(random.nextLong()));
            
            // FIX? TODO:? FIX?
            big = big.abs(); // make it positive
            
            coeffs[i] = big;
         
            // Book says "all coefficients are smaller than the modulus"
            if (modulus != null)
            {
                coeffs[i] = coeffs[i].mod(modulus);
            }
            
            // FIX? TODO: FIX? experiment says "all coefficients are smaller than the secret"
            coeffs[i] = coeffs[i].mod(secret);
        }
    }

    
    // ==================================================
    // public
    // ==================================================

    /**
     * Holds all the "publicly available" information about a secret share.
     * Holds both "required" and "optional" information.
     *
     */
    public static class PublicInfo
    {
        // the required public info: "K" and the modulus
        private final int k;                         // determines the order of the polynomial
        private final BigInteger primeModulus;       // can be null

        // useful information: "N" - how many shares were generated?
        private final int n;
        
        // just descriptive info:
        private final String description;            // any string, including null
        private final String uuid;                   // a "Random" UUID string
        private final String date;                   // yyyy-MM-dd HH:mm:ss string
        
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
         
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
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
                "date=" + date + "\n" + 
                "uuid=" + uuid +
                "]";
        }
        public String debugDump()
        {
            return toString();
        }
        public final int getN()
        {
            return n;
        }
        public final int getK()
        {
            return k;
        }
        public final BigInteger getPrimeModulus()
        {
            return primeModulus;
        }
        public final String getDescription()
        {
            return description;
        }
        public final String getUuid()
        {
            return uuid;
        }
        public final String getDate()
        {
            return date;
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
        // Identity fields:
        private final int x;              // this is aka "the index", the x in "f(x)"
        private final BigInteger share;   // our piece of the secret
        
        // "extra"
        private final PublicInfo publicInfo;
        
        public ShareInfo(final int inX,
                         final BigInteger inShare,
                         final PublicInfo inPublicInfo)
        {
            if (inShare == null)
            {
                throw new SecretShareException("share cannot be null");
            }
            if (inPublicInfo == null)
            {
                throw new SecretShareException("publicinfo cannot be null");
            }
            
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
        public final int getIndex()
        {
            return x;
        }
        public final int getX()
        {
            return x;
        }
        public final BigInteger getXasBigInteger()
        {
            return BigInteger.valueOf(x);
        }
        public final BigInteger getShare()
        {
            return share;
        }
        public final PublicInfo getPublicInfo()
        {
            return publicInfo;
        }
        @Override
        public int hashCode()
        {
            // Yes, this is a terrible implementation.   But it is correct.
            return x;
        }
        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof ShareInfo)
            {
                return equalsType((ShareInfo) obj);
            }
            else
            {
                return false;
            }
        }
        
        public boolean equalsType(ShareInfo other)
        {
            // NOTE: the "public info" does not count toward identity
            return ((this.x == other.x)  &&
                    (this.share.equals(other.share)));
        }
    }
    
    /**
     * When the secret is split, this is the information that is returned.
     * Note: This is NOT the "public" information, since the polynomial
     *       used in splitting the secret is in this object.
     *       The "public" information is the '.getShareInfos()' method.
     */
    public static class SplitSecretOutput
    {
        private final PublicInfo publicInfo;
        private final List<ShareInfo> sharesInfo = new ArrayList<ShareInfo>();
        private final PolyEquationImpl polynomial;
        
        public SplitSecretOutput(final PublicInfo inPublicInfo,
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
        public final List<ShareInfo> getShareInfos()
        {
            return Collections.unmodifiableList(sharesInfo);
        }
        public final PublicInfo getPublicInfo()
        {
            return publicInfo;
        }

    }
    
    /**
     * Holds the output of the combine() operation, i.e. the original secret.
     *
     */
    public static class CombineOutput
    {
        private final BigInteger secret;
        
        public CombineOutput(final BigInteger inSecret)
        {
            secret = inSecret;
        }
        
        public final BigInteger getSecret()
        {
            return secret;
        }
    }

    public BigInteger combineParanoid(List<ShareInfo> shares)
    {
        return combineParanoid(shares, null, System.out);
    }
    
    public BigInteger combineParanoid(List<ShareInfo> shares,
                                     Integer maximumCombinationsToTest,
                                     PrintStream outstream)
    {
        BigInteger answer = null;
        PublicInfo publicInfo = shares.get(0).getPublicInfo();
        
        CombinationGenerator<ShareInfo> combo =
            new CombinationGenerator<ShareInfo>(shares,
                                                publicInfo.getK());
        
        if (outstream != null)
        {
            outstream.println("SecretShare.paranoid(max=" +
                              maximumCombinationsToTest + 
                              " combo.total=" +
                              combo.getTotalNumberOfCombinations() +
                              ")");
        }
        
        final int percentEvery = 30;  // or 10 for every 10%
        int outputEvery = 100;
        if (maximumCombinationsToTest != null)
        {
            if (BigInteger.valueOf(maximumCombinationsToTest)
                    .compareTo(combo.getTotalNumberOfCombinations()) > 0)
            {
                maximumCombinationsToTest = combo.getTotalNumberOfCombinations().intValue();
                outputEvery = (maximumCombinationsToTest * percentEvery ) / 100 + 1;
            }
        }
        else
        {
            outputEvery = (combo.getTotalNumberOfCombinations().intValue() * percentEvery ) / 100  + 1;
        }
        
        
//        outputEvery = 1;
        int count = -1;
        for (List<SecretShare.ShareInfo> usetheseshares : combo)
        {
            count++;
            if (maximumCombinationsToTest != null)
            {
                if (count > maximumCombinationsToTest)
                {
                    break;
                }
            }
            
            if ((count % outputEvery) == 0)
            {
//                count = 0;
                
                if (outstream != null)
                {
                    outstream.println("Combination: " + 
                                      combo.getCurrentCombinationNumber() + 
                                      " of " +
                                      combo.getTotalNumberOfCombinations() +
                                      combo.getIndexesAsString() +
                                      dumpshares(usetheseshares));
                }
            }
            
            SecretShare.CombineOutput solved = this.combine(usetheseshares);
            BigInteger solve =  solved.getSecret();
            if (answer == null)
            {
                answer = solve;
            }
            else
            {
                if (! answer.equals(solve))
                {
                    throw new SecretShareException("Failed on combination, count=" + count);
                }
            }
        }
        return answer;
    }

    private String dumpshares(List<ShareInfo> usetheseshares)
    {
        String ret = "";
        for (ShareInfo share : usetheseshares)
        {
            ret += " " + share.getShare();
        }
        return ret;
    }


    
    // ==================================================
    // non public methods
    // ==================================================
}

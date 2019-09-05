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
package com.tiemens.secretshare.main.cli;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tiemens.secretshare.BuildVersion;
import com.tiemens.secretshare.engine.SecretShare.ParanoidInput;
import com.tiemens.secretshare.engine.SecretShare.PublicInfo;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import com.tiemens.secretshare.main.cli.MainCombine.CombineInput;
import com.tiemens.secretshare.main.cli.MainCombine.CombineOutput;
import com.tiemens.secretshare.main.cli.MainSplit.SplitInput;
import com.tiemens.secretshare.main.cli.MainSplit.SplitOutput;

/**
 * Test the new "-paranoid <k>" feature for the "Combine" command line.
 * That option is available in 1.4.0+ for "Split" command line already.
 *
 * @author tiemens
 *
 */
public class MainCombineParanoidTest
{

    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception
    {
        BuildVersion.disableFailureInLoad();
    }

    @AfterClass
    public static void tearDownAfterClass()
            throws Exception
    {
    }

    @Before
    public void setUp()
            throws Exception
    {
    }

    @After
    public void tearDown()
            throws Exception
    {
    }

    @Test
    public void testParanoidCombine()
    {
        final ParanoidInput paranoidAllPrintAll = ParanoidInput.parseForCombine("paranoid", "50");

        final Pair<PublicInfo, List<ShareInfo>> cats = getStaticCatSplitAddRandom(5);
        final PublicInfo publicInfo = cats.getKey();
        final List<ShareInfo> catsAndRandomShares = cats.getValue();

        CombineOutput wrongCombine = combineSimple(publicInfo, catsAndRandomShares, paranoidAllPrintAll); //  "compute all combinations"
        System.out.println("UT Paranoid agreed secret = " + wrongCombine.getParanoidOutput().getAgreedAnswer());
        wrongCombine.getParanoidOutput().printParanoidOutput(System.out);
        System.out.println(wrongCombine.getParanoidOutput().getParanoidCompleteOutput());
    }

    private Pair<PublicInfo, List<ShareInfo>> getStaticCatSplitAddRandom(int extra)
    {
        Pair<PublicInfo, List<ShareInfo>> ret = getStaticCatSplit();
        final PublicInfo publicInfo = ret.getKey();
        List<ShareInfo> shares = ret.getValue();
        int x = shares.size() + 1;
        for (int i = 0; i < extra; i++)
        {
            shares.add(new ShareInfo(x, BigInteger.valueOf(x * 50), publicInfo));
            x++;
        }
        return ret;
    }

    @Test
    public void testCatsAndDogs()
    {
        final ParanoidInput paranoidAllPrintAll = ParanoidInput.create(null);

        final Pair<PublicInfo, List<ShareInfo>> cats = getStaticCatSplit();
        final PublicInfo publicInfo = cats.getKey();
        final List<ShareInfo> catShares = cats.getValue();

        final Pair<PublicInfo, List<ShareInfo>> dogs = getStaticDogSplit();
        final List<ShareInfo> dogShares = dogs.getValue();

        final List<ShareInfo> mixedShares = new ArrayList<ShareInfo>();
        int i = 0;
        // put 4 dogs with 6 cats - combines to get 20x cats  and 4x dogs, and 96x of junk
        // it is required that k is the same value (k=3) for both cats and dogs
        for ( ; i < 4; i++)
        {
            mixedShares.add(dogShares.get(i));
        }
        for (int n = catShares.size(); i < n; i++)
        {
            mixedShares.add(catShares.get(i));
        }

        CombineOutput wrongCombine = combineSimple(publicInfo, mixedShares, paranoidAllPrintAll); //  "compute all combinations"
        System.out.println("Wrong recovered secret = " + wrongCombine.getRecoveredSecretAsString());
        System.out.println("Wrong agreed secret = " + wrongCombine.getParanoidOutput().getAgreedAnswer());
        System.out.println("Wrong number of answers = " + wrongCombine.getParanoidOutput().getReconstructedMap().size());
        wrongCombine.getParanoidOutput().printParanoidOutput(System.out);
        System.out.println(wrongCombine.getParanoidOutput().getParanoidCompleteOutput());
    }

    @Test
    public void testBasicCatCorruptOne()
    {
        //SplitOutput output = splitSimpleCat();
        final ParanoidInput paranoidFive = ParanoidInput.create(BigInteger.valueOf(5));
        final ParanoidInput paranoidAll = ParanoidInput.create(null);
        final ParanoidInput paranoidAllPrintAll = ParanoidInput.create(null);
        paranoidAllPrintAll.setOutputEvery(1);

        final Pair<PublicInfo, List<ShareInfo>> pair = getStaticCatSplit();
        final PublicInfo publicInfo = pair.getKey();
        final List<ShareInfo> shares = pair.getValue();


        CombineOutput combine = combineSimple(publicInfo, shares, null);
        //output.print(System.out);
        System.out.println("Recovered secret number = " + combine.getRecoveredSecret());
        System.out.println("Recovered secret as string = '" + combine.getRecoveredSecretAsString() + "'");
        //combine.print(System.out);

        CombineOutput paranoidCombine = combineSimple(publicInfo, shares, paranoidFive);
        System.out.println("Paranoid Computed count = " + paranoidCombine.getParanoidOutput().getCount());
        System.out.println("Paranoid Available count = " + paranoidCombine.getParanoidOutput().getTotalNumberOfCombinations());
        paranoidCombine.getParanoidOutput().printParanoidOutput(System.out);
        assertEquals(combine.getRecoveredSecret(), paranoidCombine.getRecoveredSecret());

        // Now, modify the last share to be invalid:
        List<ShareInfo> wronglast = new ArrayList<ShareInfo>();
        wronglast.addAll(shares);
        ShareInfo last = wronglast.remove(wronglast.size() - 1);
        BigInteger wrongval = BigInteger.valueOf(51234L);
        ShareInfo wrong = new ShareInfo(last.getX(), wrongval, publicInfo);
        wronglast.add(wrong);
        CombineOutput wrongCombine = combineSimple(publicInfo, wronglast, paranoidAllPrintAll); //  "compute all combinations"
        System.out.println("Wrong recovered secret = " + wrongCombine.getRecoveredSecretAsString());
        System.out.println("Wrong agreed secret = " + wrongCombine.getParanoidOutput().getAgreedAnswer());
        System.out.println("Wrong number of answers = " + wrongCombine.getParanoidOutput().getReconstructedMap().size());
        wrongCombine.getParanoidOutput().printParanoidOutput(System.out);
        System.out.println(wrongCombine.getParanoidOutput().getParanoidCompleteOutput());
/*
        Map<BigInteger, Integer> map = wrongCombine.getParanoidOutput().getReconstructedMap();
        for (BigInteger secret : map.keySet())
        {
            Integer count = map.get(secret);
            System.out.println("count=" + count + "  times came up with " + secret + " string=" + BigIntUtilities.Human.createHumanString(secret));
        }
        */

    }

    private Pair<PublicInfo, List<ShareInfo>> getStaticCatSplit()
    {
        BigInteger modulus = new BigInteger(smallPrime);
        final int n = 6;
        final int k = 3;

        PublicInfo publicInfo = new PublicInfo(n, k, modulus, "static");
        List<ShareInfo> shares = new ArrayList<ShareInfo>();

        shares.add(new ShareInfo(1, new BigInteger("11317700"), publicInfo));
        shares.add(new ShareInfo(2, new BigInteger("24138244"), publicInfo));
        shares.add(new ShareInfo(3, new BigInteger("42877492"), publicInfo));
        shares.add(new ShareInfo(4, new BigInteger("67535444"), publicInfo));
        shares.add(new ShareInfo(5, new BigInteger("98112100"), publicInfo));
        shares.add(new ShareInfo(6, new BigInteger("134607460"), publicInfo));
        shares.add(new ShareInfo(7, new BigInteger("177021524"), publicInfo));
        shares.add(new ShareInfo(8, new BigInteger("225354292"), publicInfo));
        shares.add(new ShareInfo(9, new BigInteger("279605764"), publicInfo));
        shares.add(new ShareInfo(10, new BigInteger("339775940"), publicInfo));

        Pair<PublicInfo, List<ShareInfo>> ret = new Pair<PublicInfo, List<ShareInfo>>(publicInfo, shares);

        return ret;
    }

    private Pair<PublicInfo, List<ShareInfo>> getStaticDogSplit()
    {
        BigInteger modulus = new BigInteger(smallPrime);
        final int n = 6;
        final int k = 3;

        PublicInfo publicInfo = new PublicInfo(n, k, modulus, "static");
        List<ShareInfo> shares = new ArrayList<ShareInfo>();

        shares.add(new ShareInfo(1, new BigInteger("5446929"), publicInfo));
        shares.add(new ShareInfo(2, new BigInteger("8221875"), publicInfo));
        shares.add(new ShareInfo(3, new BigInteger("12809805"), publicInfo));
        shares.add(new ShareInfo(4, new BigInteger("19210719"), publicInfo));
        shares.add(new ShareInfo(5, new BigInteger("27424617"), publicInfo));
        shares.add(new ShareInfo(6, new BigInteger("37451499"), publicInfo));

        Pair<PublicInfo, List<ShareInfo>> ret = new Pair<PublicInfo, List<ShareInfo>>(publicInfo, shares);

        return ret;
    }

    private final String smallPrime = "16639793";

    public SplitOutput splitSimpleCat()
    {
        //$ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat"
        String[] args = {/*"split",*/ "-k", "3", "-n", "6", "-m", smallPrime, "-sS", "Cat"};

        SplitInput input = SplitInput.parse(args);
        SplitOutput output = input.output();



        return output;
    }

    public CombineOutput combineSimple(PublicInfo publicInfo, List<ShareInfo> shares, ParanoidInput paranoidInput)
    {
        //String[] args = {/*"combine",*/ "-k", "" + publicInfo.getK(), "-n", "" + publicInfo.getN(), "-m", "" + publicInfo.getPrimeModulus() };
        //CombineInput inputargs = CombineInput.parse(args, null, null);
        CombineInput input = new CombineInput(publicInfo.getK(), publicInfo.getN(), publicInfo, paranoidInput);
        //System.out.println("K=" + publicInfo.getK());
        //System.out.println("MOD=" + publicInfo.getPrimeModulus());
        /*for (int i = 0; i < publicInfo.getK(); i++)
        {
            input.addIfNotDuplicate(shares.get(i));
        }*/
        for (ShareInfo share : shares)
        {
            input.addIfNotDuplicate(share);
        }

        CombineOutput output = input.output();
        return output;
    }

    public static class Pair<K, V>
    {

        private final K key;
        private final V value;

        public Pair(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        public K getKey()
        {
            return key;
            }

        public V getValue()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return key + "=" + value;
        }

        @Override
        public int hashCode()
        {
            return key.hashCode() * 17 + (value == null ? 0 : value.hashCode());
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o instanceof Pair)
            {
                Pair<?, ?> pair = (Pair<?, ?>) o;
                if (key != null ? !key.equals(pair.key) : pair.key != null)
                {
                    return false;
                }
                if (value != null ? !value.equals(pair.value) : pair.value != null)
                {
                    return false;
                }
                return true;
            }
            return false;
        }
    } // Pair

}

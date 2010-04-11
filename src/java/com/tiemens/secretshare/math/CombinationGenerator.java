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
package com.tiemens.secretshare.math;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.tiemens.secretshare.exceptions.SecretShareException;

public class CombinationGenerator<E> 
        implements Iterator<List<E>>, 
                   Iterable<List<E>> 
{
    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================

    public static void main(String[] args) 
    {
        System.out.println("fact(5)=" + factorial(5));
        System.out.println("fact(3)=" + factorial(3));
        List<String> list = Arrays.asList("1", "2", "3", "4", "5");
        CombinationGenerator<String> combos = new CombinationGenerator<String>(list, 3);
        int count = 1;
        System.out.println("Total number=" + combos.getTotalNumberOfCombinations());
        for(List<String> combination : combos) 
        {
            System.out.println(count + ": " + combination + " {" + combos.indexesAsString + "}");
            count++;
        }
    }

    // ==================================================
    // instance data
    // ==================================================

    private final List<E> list;
    
    // currentIndexes contains the indexes to use for the NEXT iteration
    private int[] currentIndexes;
    
    // indexesAsString contains either null or the CURRENT iteration's indexes
    private String indexesAsString;
    
    // 
    private final BigInteger totalNumberOfCombinations;
    // ranges 0 to totalNumber, where "0" means "you haven't called next() yet"
    private BigInteger combinationNumber;
    
    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    public CombinationGenerator(final List<E> inList, 
                                final int inChoiceSize) 
    {
        if (inChoiceSize < 1)
        {
            throw new SecretShareException("choice size cannot be less than 1:" + inChoiceSize);
        }
        
        if (inChoiceSize > inList.size()) 
        {
            throw new SecretShareException("choice size cannot be greater than size");
        }
        
        List<E> ourlist = new ArrayList<E>(inList);
        
        this.list = Collections.unmodifiableList(ourlist);
        
        this.currentIndexes = new int[inChoiceSize];
        for (int i = 0; i < inChoiceSize; i++) 
        {
            this.currentIndexes[i] = i;
        }
        
        totalNumberOfCombinations = computeNfactdivkNkFact(this.list.size(), inChoiceSize);
        combinationNumber = BigInteger.ZERO;
    }

    

    /**
     * Return (n!) / ( k! * (n-k)! ).
     * 
     * @param n
     * @param k
     * @return
     */
    private BigInteger computeNfactdivkNkFact(final int n,
                                              final int k)
    {
        final int nminusk = n - k;
        
        BigInteger kfactTimesnmkfact = factorial(k).multiply(factorial(nminusk));
        BigInteger nfactorial = factorial(n);
        
        return nfactorial.divide(kfactTimesnmkfact);
    }



    // ==================================================
    // public methods
    // ==================================================

    public final BigInteger getCurrentCombinationNumber()
    {
        return combinationNumber;
    }
    
    public final BigInteger getTotalNumberOfCombinations()
    {
        return totalNumberOfCombinations;
    }

    @Override
    public Iterator<List<E>> iterator() 
    {
        return this;
    }

    @Override
    public boolean hasNext() 
    {
        return (currentIndexes != null);
    }

    public String getIndexesAsString()
    {
        return indexesAsString;
    }    

    @Override
    public List<E> next() 
    {
        if (! hasNext()) 
        {
            // ouch - Java's exception type design makes this a hard choice:
            //        It would be nice to throw new SecretShareException() here.
            throw new NoSuchElementException();
        }

        combinationNumber = combinationNumber.add(BigInteger.ONE);
        
        List<E> currentCombination = new ArrayList<E>();
        for (int i : currentIndexes) 
        {
            currentCombination.add(list.get(i));
        }
        
        // capture before moving the indexes:
        indexesAsString = Arrays.toString(currentIndexes);
        
        moveIndexesToNextCombination();
        
        return currentCombination;
    }

    public void remove() 
    {
        // ouch again
        throw new UnsupportedOperationException();
    }
    
    // ==================================================
    // private methods
    // ==================================================
    
    private void moveIndexesToNextCombination() 
    {
        for (int i = currentIndexes.length - 1, j = list.size() - 1; i >= 0; i--, j--) 
        {
            if (currentIndexes[i] != j) 
            {
                currentIndexes[i]++;
                for (int k = i + 1; k < currentIndexes.length; k++) 
                {
                    currentIndexes[k] = currentIndexes[k-1] + 1;
                }
                return;
            }
        }
        // otherwise, we are all done:
        currentIndexes = null;
    }
//    int i = r - 1;
//    while (a[i] == (n - r + i)) 
//    {
//        i--;
//    }
//    a[i] = a[i] + 1;
//    for (int j = i + 1; j < r; j++) 
//    {
//        a[j] = a[i] + j - i;
//    }
    
    private static BigInteger factorial (int n) 
    {
        BigInteger ret = BigInteger.ONE;
        for (int i = n; i > 1; i--) 
        {
            ret = ret.multiply(BigInteger.valueOf(i));
        }
        return ret;
    }



}

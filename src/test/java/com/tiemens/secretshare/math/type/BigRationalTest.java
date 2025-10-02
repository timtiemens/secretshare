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
package com.tiemens.secretshare.math.type;


import java.math.BigInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BigRationalTest
{

    @BeforeAll
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception
    {
    }

    @BeforeEach
    public void setUp() throws Exception
    {
    }

    @AfterEach
    public void tearDown() throws Exception
    {
    }

    @Test
    public void test12p13e56()
    {
        BigRational actual = new BigRational(1, 2).add(new BigRational(1, 3));
        assertEquals(new BigRational(5, 6), actual);
    }

    @Test
    public void test23p13e1()
    {
        BigRational actual = new BigRational(2, 3).add(new BigRational(1, 3));
        assertEquals(new BigRational(1), actual);
    }

    @Test
    public void test2kp3ke12k()
    {
        BigRational actual = new BigRational(1, 200000000).add(new BigRational(1, 300000000));
        assertEquals(new BigRational(1, 120000000), actual);
    }

    @Test
    public void test120p130e112()
    {
        BigRational actual = new BigRational(1073741789, 20).add(new BigRational(1073741789, 30));
        assertEquals(new BigRational(1073741789, 12), actual);
    }

    @Test
    public void test417t174e1()
    {
        BigRational actual = new BigRational(4, 17).multiply(new BigRational(17, 4));
        assertEquals(new BigRational(1), actual);
    }

    @Test
    public void test3k3t3k3e841961()
    {
        BigRational actual = new BigRational(3037141, 3247033).multiply(new BigRational(3037547, 3246599));
        assertEquals(new BigRational(841, 961), actual);
    }

    @Test
    public void test16m48e13()
    {
        BigRational actual = new BigRational(1, 6).subtract(new BigRational(-4, -8));
        assertEquals(new BigRational(-1, 3), actual);
    }

    @Test
    public void testn12pn13en6()
    {
        BigRational actual = new BigRational(-1, 200000000).add(new BigRational(1, 300000000));
        assertEquals(new BigRational(-1, 600000000), actual);
    }

    @Test
    public void testn13t79e727()
    {
        BigRational actual = new BigRational(1, 3).multiply(new BigRational(7, 9));
        assertEquals(new BigRational(7, 27), actual);
    }

    @Test
    public void testn13compareto79()
    {
        BigRational oneThree = new BigRational(1, 3);
        BigRational sevenNine = new BigRational(7, 9);
        assertEquals(-1, oneThree.compareTo(sevenNine));
        assertEquals(1,  sevenNine.compareTo(oneThree));
    }


    @Test
    public void testZero()
    {
        BigRational actual = new BigRational(0, 5);
        assertEquals(BigRational.ZERO, actual);
        assertEquals(BigRational.ZERO, actual.add(actual));

        try
        {
            actual.reciprocal();
            fail("1/0 should throw exception");
        }
        catch (ArithmeticException e)
        {
            // ok
        }
    }

    @Test
    public void testZeroEquivs()
    {
        assertIsCanonicalZero(new BigRational(1, 3).add(new BigRational(-1, 3)));
        assertIsCanonicalZero(new BigRational(1, 3).multiply(new BigRational(-3)).add(new BigRational(80, 80)));
    }

    private void assertIsCanonicalZero(BigRational zero)
    {
        assertEquals(BigInteger.ZERO, zero.getNumerator());
        assertEquals(BigInteger.ONE, zero.getDenominator());
    }

    @Test
    public void testGCDs()
    {
        assertGCD(new BigRational(8, 80), 1, 10);
        assertGCD(new BigRational(54, 2), 27, 1);
    }

    private void assertGCD(BigRational bigRational, int num, int denom)
    {
        assertEquals(BigInteger.valueOf(num), bigRational.getNumerator());
        assertEquals(BigInteger.valueOf(denom), bigRational.getDenominator());
    }
}

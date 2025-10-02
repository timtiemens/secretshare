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
package com.tiemens.secretshare.math.matrix;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tiemens.secretshare.math.type.BigRational;

public class NumberSimplexTest
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

    // 2 -1 1 = 2
    // 1 2 -1 = 3
    // 3 1 2 = -1
    // answer x=2 y=-1 z=-3
    @Test
    public void test211()
    {
        final int i = 3; // n
        final int j = 4; // m
        BigRationalMatrix matrix = new BigRationalMatrix(i, j);
        matrix.fill(j, 2, -1, 1,    2,
                       1,  2, -1,   3,
                       3,  1, 2,    -1
                       );
        matrix.printResult(System.out);
        NumberSimplex<BigRational> simplex = null;
        simplex = new NumberSimplex<BigRational>(matrix, 3);
        simplex.initForSolve(System.out);
        simplex.solve(System.out);
        assertEquals(new BigRational(2), simplex.getAnswer(0));
        assertEquals(new BigRational(-1), simplex.getAnswer(1));
        assertEquals(new BigRational(-3), simplex.getAnswer(2));

    }

    // 1 -3  1 = -2
    // 2  1 -1 = 6
    // 1  2  2 = 2
    //   ANSWER: x=2 y=1 c=-1
    @Test
    public void test131()
    {
        final int i = 3; // n
        final int j = 4; // m
        BigRationalMatrix matrix = new BigRationalMatrix(i, j);
        matrix.fill(j, 1, -3, 1,    -2,
                       2,  1, -1,    6,
                       1,  2,  2,    2
                       );
        matrix.printResult(System.out);
        NumberSimplex<BigRational> simplex = null;
        simplex = new NumberSimplex<BigRational>(matrix, 3);
        simplex.initForSolve(System.out);
        simplex.solve(System.out);
        assertEquals(new BigRational(2), simplex.getAnswer(0));
        assertEquals(new BigRational(1), simplex.getAnswer(1));
        assertEquals(new BigRational(-1), simplex.getAnswer(2));
    }


    // 1 -1 1 = 2
    // 1 1 0 = 1
    // 1 1 1 = 8
    //   ANSWER: x=-2 y=3 c=7
    @Test
    public void test111()
    {
        final int i = 3; // n
        final int j = 4; // m
        BigRationalMatrix matrix = new BigRationalMatrix(i, j);
        matrix.fill(j, 1, -1, 1,    2,
                       1,  1, 0,    1,
                       1,  1, 1,    8
                       );
        matrix.printResult(System.out);
        NumberSimplex<BigRational> simplex = null;
        simplex = new NumberSimplex<BigRational>(matrix, 3);
        simplex.initForSolve(System.out);
        simplex.solve(System.out);
        assertEquals(new BigRational(-2), simplex.getAnswer(0));
        assertEquals(new BigRational(3), simplex.getAnswer(1));
        assertEquals(new BigRational(7), simplex.getAnswer(2));
    }

    // 3 -6 1 = 7
    // 1 2 1 = 5
    // -2 5 -2 = -1
    //   ANSWER: x=5 y=1 c=-2
    @Test
    public void test361()
    {
        final int i = 3; // n
        final int j = 4; // m
        BigRationalMatrix matrix = new BigRationalMatrix(i, j);
        matrix.fill(j, 3, -6, 1,    7,
                       1,  2, 1,    5,
                       -2, 5, -2,   -1
                       );
        matrix.printResult(System.out);
        NumberSimplex<BigRational> simplex = null;
        simplex = new NumberSimplex<BigRational>(matrix, 3);
        simplex.initForSolve(System.out);
        simplex.solve(System.out);
        assertEquals(new BigRational(5), simplex.getAnswer(0));
        assertEquals(new BigRational(1), simplex.getAnswer(1));
        assertEquals(new BigRational(-2), simplex.getAnswer(2));
    }
}

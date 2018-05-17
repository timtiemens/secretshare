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



import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tiemens.secretshare.math.BigRational;

public class BigRationalMatrixTest
{

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void test()
    {
        final int i = 2;
        final int j = 3;
        BigRationalMatrix matrix = new BigRationalMatrix(i, j);
        matrix.fill(j, 1, 2, 3,
                       4, 5, 6);
        matrix.printResult(System.out);
        BigRational det1122 = matrix.determinant(matrix.getArray(), 0, 0, 1, 1);
        Assert.assertEquals(new BigRational(5 - 8), det1122);
        BigRational det1123 = matrix.determinant(matrix.getArray(), 0, 0, 1, 2);
        Assert.assertEquals(new BigRational(6 - 12), det1123);
    }

    @Test
    public void testAdd()
    {
        final int h = 2;
        final int w = 3;
        BigRationalMatrix matrixA = new BigRationalMatrix(h, w);
        matrixA.fill(w,  1, 2, 3,
                         4, 5, 6);
        BigRationalMatrix matrix1 = new BigRationalMatrix(h, w);
        matrix1.fill(w,  1, 1, 1,
                         1, 1, 1);

        BigRational[][] matrixAarray = matrixA.getArray();
        BigRational[][] matrix1array = matrix1.getArray();

        BigRational[][] actual = matrixA.addMatrix(matrixAarray, matrix1array);

        Assert.assertEquals("i dim", h, actual.length);
        Assert.assertEquals("j dim", w, actual[0].length);
        for (int y = 0; y < h; y++) {
            for (int x = 0;  x < w; x++) {
                BigRational expected = matrixAarray[y][x].add(matrix1array[y][x]);
                Assert.assertEquals("y=" + y + " x=" + x, expected, actual[y][x]);
            }
        }
    }

    @Test
    public void testMultiply()
    {
        final int h = 2;
        final int w = 3;
        BigRationalMatrix matrixA = new BigRationalMatrix(h, w);
        matrixA.fill(w,  1, 2, 3,
                         4, 5, 6);
        // note: reversed!
        BigRationalMatrix matrixB = new BigRationalMatrix(w, h);
        matrixB.fill(h,  7, 8,
                         9, 10,
                        11, 12);

        // note: reversed!
        BigRationalMatrix expected = new BigRationalMatrix(h, h);
        expected.fill(h,  58, 64,
                          139, 154);


        BigRational[][] actualArray = matrixA.multiplyMatrix(matrixA.getArray(), matrixB.getArray());
        BigRationalMatrix actual = new BigRationalMatrix(actualArray);

        subtestCompare(expected, actual);
    }

    private void subtestCompare(BigRationalMatrix expected, BigRationalMatrix actual)
    {
        BigRational[][] expectedArray = expected.getArray();
        BigRational[][] actualArray = actual.getArray();

        Assert.assertEquals("i dim", expectedArray.length, actualArray.length);
        Assert.assertEquals("j dim", expectedArray[0].length, actualArray[0].length);
        int h = expectedArray.length;
        int w = expectedArray[0].length;

        for (int y = 0; y < h; y++) {
            for (int x = 0;  x < w; x++) {
                Assert.assertEquals("y=" + y + " x=" + x, expectedArray[y][x], actualArray[y][x]);
            }
        }
    }
}

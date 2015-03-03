package com.tiemens.secretshare.math.matrix;



import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tiemens.secretshare.math.BigRational;

public class BigRationalMatrixTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        final int i = 2;
        final int j = 3;
        BigRationalMatrix matrix = new BigRationalMatrix(i, j);
        matrix.fill(j, 1, 2, 3,
                       4, 5, 6);
        matrix.printResult(System.out);
        BigRational det1122 = matrix.determinant(matrix.getArray(), 0, 0, 1, 1);
        Assert.assertEquals(new BigRational(5 - 8), det1122);
        BigRational det1122 = matrix.determinant(matrix.getArray(), 0, 0, 1, 1);
        Assert.assertEquals(new BigRational(5 - 8), det1122);
    }

}

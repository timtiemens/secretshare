package com.tiemens.secretshare.math.matrix;






import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tiemens.secretshare.math.BigRational;

public class NumberSimplexTest {

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

    // 2 -1 1 = 2
    // 1 2 -1 = 3
    // 3 1 2 = -1
    // answer x=2 y=-1 z=-3
    @Test
    public void test211() {
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
        Assert.assertEquals(new BigRational(2), simplex.getAnswer(0));
        Assert.assertEquals(new BigRational(-1), simplex.getAnswer(1));
        Assert.assertEquals(new BigRational(-3), simplex.getAnswer(2));

    }

    // 1 -3  1 = -2
    // 2  1 -1 = 6
    // 1  2  2 = 2
    //   ANSWER: x=2 y=1 c=-1
    @Test
    public void test131() {
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
        Assert.assertEquals(new BigRational(2), simplex.getAnswer(0));
        Assert.assertEquals(new BigRational(1), simplex.getAnswer(1));
        Assert.assertEquals(new BigRational(-1), simplex.getAnswer(2));
    }


    // 1 -1 1 = 2
    // 1 1 0 = 1
    // 1 1 1 = 8
    //   ANSWER: x=-2 y=3 c=7
    @Test
    public void test111() {
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
        Assert.assertEquals(new BigRational(-2), simplex.getAnswer(0));
        Assert.assertEquals(new BigRational(3), simplex.getAnswer(1));
        Assert.assertEquals(new BigRational(7), simplex.getAnswer(2));
    }

    // 3 -6 1 = 7
    // 1 2 1 = 5
    // -2 5 -2 = -1
    //   ANSWER: x=5 y=1 c=-2
    @Test
    public void test361() {
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
        Assert.assertEquals(new BigRational(5), simplex.getAnswer(0));
        Assert.assertEquals(new BigRational(1), simplex.getAnswer(1));
        Assert.assertEquals(new BigRational(-2), simplex.getAnswer(2));
    }
}

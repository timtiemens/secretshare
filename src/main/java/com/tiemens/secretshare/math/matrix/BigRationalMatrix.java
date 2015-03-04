package com.tiemens.secretshare.math.matrix;

import java.math.BigInteger;

import com.tiemens.secretshare.math.BigRational;

public class BigRationalMatrix extends NumberMatrix<BigRational> {

    protected BigRationalMatrix(BigRational[][] in) {
        super(in);
    }

    public BigRationalMatrix(int height, int width) {
        super(height, width);
    }

    public static BigRationalMatrix create(BigInteger[][] matrix) {
        final int height = matrix.length;
        final int width = matrix[0].length;
        BigRational[][] in = new BigRational[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                in[i][j] = new BigRational(matrix[i][j]);
            }
        }
        return new BigRationalMatrix(in);
    }

    @Override
    protected BigRational[][] create(int height, int width) {
        return new BigRational[height][width];
    }

    @Override
    protected BigRational zero() {
        return BigRational.ZERO;
    }

    @Override
    protected BigRational one() {
        return BigRational.ONE;
    }


    @Override
    protected BigRational add(BigRational o1, BigRational o2) {
        return o1.add(o2);
    }

    @Override
    protected BigRational subtract(BigRational o1, BigRational o2) {
        return o1.subtract(o2);
    }

    @Override
    protected BigRational multiply(BigRational o1, BigRational o2) {
        return o1.multiply(o2);
    }

    @Override
    protected BigRational reciprocal(BigRational o1) {
        return o1.reciprocal();
    }

    @Override
    protected BigRational negate(BigRational o1) {
        return o1.negate();
    }

    @Override
    protected BigRational createValue(int v) {
        return new BigRational(v);
    }




}

package com.tiemens.secretshare.math.matrix;

import com.tiemens.secretshare.math.BigRational;

public class BigRationalMatrix extends NumberMatrix<BigRational> {

    protected BigRationalMatrix(BigRational[][] in) {
        super(in);
    }

    public BigRationalMatrix(int i, int j) {
        super(i, j);
    }


    @Override
    protected BigRational[][] create(int i, int j) {
        return new BigRational[i][j];
    }

    @Override
    protected BigRational zero() {
        return BigRational.ZERO;
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
    protected BigRational createValue(int v) {
        return new BigRational(v);
    }


}

package com.tiemens.secretshare.math.matrix;

import java.util.List;


public abstract class NumberSimplex <E extends Number> {

    /** Abstract method to create a matrix */
    private final NumberMatrix<E> A;
    private final List<E> bConstants;


    public NumberSimplex(NumberMatrix<E> inMatrix, List<E> constants)
    {
        A = inMatrix;
        bConstants = constants;
    }

    public void solve()
    {
        int width = A.getWidth();
        int height = A.getHeight();
        NumberOrVariable<E>[] top = fillInTopVariables(width);
        NumberOrVariable<E>[] side = fillInConstants(width); // new NumberOrVariable[height];

        // fill in top with "x", "y", "z"
        // fill in side with the "b" or "="s numbers
    }

    private NumberOrVariable<E>[] createNumberOrVariable(int size)
    {
        return new NumberOrVariable[size];
    }

    private NumberOrVariable<E>[] fillInConstants(int width) {
        NumberOrVariable<E>[] ret = createNumberOrVariable(width);
        for (int i = 0; i < width; i++)
        {
            ret[i] = new NumberOrVariable<E>(bConstants.get(i));
        }
        return ret;
    }

    private NumberOrVariable<E>[] fillInTopVariables(int width) {
        NumberOrVariable<E>[] ret = createNumberOrVariable(width);
        for (int i = 0; i < width; i++)
        {
            ret[i] = new NumberOrVariable<E>(Integer.toHexString(i + 10));
        }
        return null;
    }

    public static class NumberOrVariable<E>
    {
        private E number;
        private String variable;

        public NumberOrVariable(E in)
        {
            if (in == null)
            {
                throw new IllegalArgumentException("cannot be null");
            }
            number = in;
        }
        public NumberOrVariable(String name)
        {
            if (name == null)
            {
                throw new IllegalArgumentException("cannot be null");
            }
            variable = name;
        }
        public boolean isNumber()
        {
            return number != null;
        }
        public boolean isVariable()
        {
            return variable != null;
        }
        public E getNumber()
        {
            return number;
        }
        public String getVariable()
        {
            return variable;
        }
    }

    // Samples for testing
    // 2 -1 1 = 2
    // 1 2 -1 = 3
    // 3 1 2 = -1
    // answer x=2 y=-1 z=-3

    // 1 -3 1 = -2
    // 2 1 -1 = 6
    // 1 2 2 = 2

    // 1 -1 1 = 2
    // 1 1 0 = 1
    // 1 1 1 = 8

    // 3 -6 1 = 7
    // 1 2 1 = 5
    // -2 5 -2 = -1

    // AX = B
    // pivot on a(ij) to obtain
    // AbarXbar = Bbar
    // Abar = a(rs) =
    //   if r!=i and s!=j   (1/a(ij))*det(a(rs) a(rj)
    //                                    a(is) a(ij))
    //   if r!=1 and s==j   a(rj) / a(ij)
    //   if r==i and s!=j   -1*a(is)/ a(ij)
    //   if r==i and s==j    1/a(ij)
    // Bbar = (b1 .. b(i-1) x(j) b(i+1) ... b(m))
    // Xbar = (x1 .. x(j-1) b(j) x(j+1) ... x(n))  // TODO: b(j)?? or b(i)  or x(i) above?
    //
    //
    // det(c11 c12)
    //    (c21 c22)  = c11*c22 - c21*c12
}

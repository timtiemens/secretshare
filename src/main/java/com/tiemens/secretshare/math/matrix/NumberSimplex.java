package com.tiemens.secretshare.math.matrix;


public abstract class NumberSimplex <E extends Number> {

    /** Abstract method to create a matrix */
    private final NumberMatrix<E> A;


    public NumberSimplex(NumberMatrix<E> inMatrix)
    {
        A = inMatrix;
    }

    public void solve()
    {
        int width = A.getWidth();
        int height = A.getHeight();
        NumberOrVariable[] top = new NumberOrVariable[width];
        NumberOrVariable[] side = new NumberOrVariable[height];

        // fill in top with "x", "y", "z"
        // fill in side with the "b" or "="s numbers
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
    //   if r!=i and s!=j   1/a(ij)*det(a(rs) a(rj)
    //                                  a(is) a(ij))
    //   if r!=1 and s==j   a(rj) / a(ij)
    //   if r==i and s!=j   -1*a(is)/ a(ij)
    //   if r==i and s==j    1/a(ij)
    // Bbar = (b1 .. b(i-1) x(j) b(i+1) ... b(m))
    // Xbar = (x1 .. x(j-1) b(j) x(j+1) ... x(n))
    //
    //
    // det(c11 c12)
    //    (c21 c22)  = c11*c22 - c21*c12
}

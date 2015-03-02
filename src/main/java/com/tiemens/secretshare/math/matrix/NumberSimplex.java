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
}

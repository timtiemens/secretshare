package com.tiemens.secretshare.math.matrix;

import java.io.PrintStream;

public abstract class NumberMatrix <E extends Number> {

    /** Abstract method to create a matrix */
    protected abstract E[][] create(int i,int j);

    /** Abstract method for defining zero for the matrix element */
    protected abstract E zero();

    /** Abstract method for adding two elements of the matrices */
    protected abstract E add(E o1, E o2);

    /** Abstract method for multiplying two elements of the matrices */
    protected abstract E multiply(E o1, E o2);


    /** Add two matrices */
    public E[][] addMatrix(E[][] matrix1, E[][] matrix2) {
      // Check bounds of the two matrices
      if ((matrix1.length != matrix2.length) ||
          (matrix1[0].length != matrix2[0].length)) {
        throw new RuntimeException(
          "The matrices do not have the same size");
      }

      E[][] result = //(E[][])new Number[matrix1.length][matrix1[0].length];
              create(matrix1.length, matrix1[0].length);

      // Perform addition
      for (int i = 0; i < result.length; i++) {
        for (int j = 0; j < result[i].length; j++) {
          result[i][j] = add(matrix1[i][j], matrix2[i][j]);
        }
      }

      return result;
    }

    /** Multiply two matrices */
    public E[][] multiplyMatrix(E[][] matrix1, E[][] matrix2) {
      // Check bounds
      if (matrix1[0].length != matrix2.length) {
        throw new RuntimeException(
          "The matrices do not have compatible size");
      }

      // Create result matrix
      E[][] result = //(E[][]) new Number[matrix1.length][matrix2[0].length];
              create(matrix1.length, matrix2[0].length);

      // Perform multiplication of two matrices
      for (int i = 0; i < result.length; i++) {
        for (int j = 0; j < result[0].length; j++) {
          result[i][j] = zero();

          for (int k = 0; k < matrix1[0].length; k++) {
            result[i][j] = add(result[i][j],
                               multiply(matrix1[i][k], matrix2[k][j]));
          }
        }
      }

      return result;
    }

    /** Print matrices, the operator, and their operation result */
    public static void printResult(Number[][] m1, Number[][] m2, Number[][] m3, char op, PrintStream out) {
      for (int i = 0; i < m1.length; i++) {
        for (int j = 0; j < m1[0].length; j++) {
          out.print(" " + m1[i][j]);
        }

        if (i == m1.length / 2)
          out.print("  " + op + "  ");
        else
          out.print("     ");

        for (int j = 0; j < m2.length; j++) {
          out.print(" " + m2[i][j]);
        }

        if (i == m1.length / 2)
          out.print("  =  ");
        else
          out.print("     ");

        for (int j = 0; j < m3.length; j++) {
          out.print(m3[i][j] + " ");
        }

        out.println();
      }
    }
}

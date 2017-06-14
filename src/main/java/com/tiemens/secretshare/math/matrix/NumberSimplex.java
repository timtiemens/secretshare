/*******************************************************************************
 * $Id: $
 * Copyright (c) 2009-2017 Tim Tiemens.
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
 ******************************************************************************/
package com.tiemens.secretshare.math.matrix;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NumberSimplex <E extends Number>
{

    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================

    /** Abstract method to create a matrix */
    private final NumberMatrix<E> matrix;
    /** index of the "B" column - i.e. the index of the constants */
    private final int constantsInThisColumnIndex; // 0-based index

    // computed values
    private NumberOrVariable<E>[] mTop;
    private NumberOrVariable<E>[] mSide;
    private E[][] mArrayhide;
    private Map<NumberOrVariable<E>, E> mAnswers;

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================


    public NumberSimplex(NumberMatrix<E> inMatrix, int inConstantsIndex)
    {
        matrix = inMatrix;
        constantsInThisColumnIndex = inConstantsIndex;
        //System.out.println("SIMPLEX cons matrix.h=" + matrix.getHeight() + " matrix.w=" + matrix.getWidth() + " m.array.length=" +
        //                   matrix.getArray().length + " m.array[0].length=" + matrix.getArray()[0].length);

    }


    // ==================================================
    // public methods
    // ==================================================

    // .initForSolve(), then .solve(), then .getAnswer()

    public void initForSolve(PrintStream out)
    {
        final int width = matrix.getWidth();
        final int height = matrix.getHeight();

        mTop = fillInTopVariables(width - 1);
        mSide = fillInConstants(height);
        //System.out.println("INIT-SOLVE matrix.h=" + matrix.getHeight() + " matrix.w=" + matrix.getWidth() + " m.array.length=" +
        //        matrix.getArray().length + " m.array[0].length=" + matrix.getArray()[0].length);
        mArrayhide = fillInArray(matrix.getArray(), constantsInThisColumnIndex);

        nullPrintln(out, "INIT-SOLVE, TOP.length = " + mTop.length + " matrix.height=" + height + " matrix.w=" + width);
        printTopArraySide(out);

    }

    public void solve(PrintStream out)
    {
        final int height = mArrayhide.length;
        final int width = mArrayhide[0].length;
        if (height != width)
        {
            throw new ArithmeticException("h=" + height + " w=" + width + " must match");
        }

        final int numberOfPivots = width;

        for (int p = 0; p < numberOfPivots; p++)
        {
            Pairij pairij = findPivot(mArrayhide);
            pivot(pairij.i, pairij.j);

            nullPrintln(out, "PIVOT COMPLETE, #" + p + " pivot=" + pairij);
            printTopArraySide(out);
        }
        mAnswers = computeAnswers(mTop, mArrayhide, mSide);
        printAnswers(out, mAnswers);
    }

    /**
     * @param i range 0-to-
     * @return answer
     */
    public E getAnswer(int i)
    {
        E ret = mAnswers.get(createVariableForIndex(i));
        if (ret != null)
        {
            return ret;
        }
        else
        {
            throw new ArithmeticException("Could not find answer at index " + i + " answers=" + mAnswers);
        }
    }

    public E getConstantNumbered(int j)
    {
        return matrix.getArray()[j][constantsInThisColumnIndex];
    }


    // ==================================================
    // non public methods
    // ==================================================

    private void nullPrintln(PrintStream out, String line)
    {
        if (out != null)
        {
            out.println(line);
        }
    }
    private void printTopArraySide(PrintStream out)
    {
        print(mTop, mArrayhide, mSide, out);
    }


    private void printAnswers(PrintStream out, Map<NumberOrVariable<E>, E> answers)
    {
        if (out == null)
        {
            return;
        }
        for (NumberOrVariable<E> var : answers.keySet())
        {
            out.print(var);
            out.print("=");
            out.print(answers.get(var));
            out.println("");
        }
    }

    private Map<NumberOrVariable<E>, E> computeAnswers(NumberOrVariable<E>[] tops,
                                                       E[][] array,
                                                       NumberOrVariable<E>[] sides)
    {
        Map<NumberOrVariable<E>, E> ret = new HashMap<NumberOrVariable<E>, E>();

        // sanity checks:
        for (NumberOrVariable<E> v : tops)
        {
            if (v.isVariable())
            {
                throw new ArithmeticException("Not a number: " + v);
            }
        }
        for (NumberOrVariable<E> v : sides)
        {
            if (v.isNumber())
            {
                throw new ArithmeticException("Not a variable: " + v);
            }
        }
        final int height = array.length;
        final int width = array[0].length;
        for (int i = 0; i < height; i++)
        {
            E answer = zero();
            for (int j = 0; j < width; j++)
            {
                answer = add(answer, multiply(array[i][j], tops[j].getNumber()));
            }

            ret.put(sides[i], answer);
        }
        return ret;
    }



    private void pivot(int i, int j)
    {
        mArrayhide = createAbar(mArrayhide, i, j);

        // Swap top and side
        NumberOrVariable<E> tmp = mSide[i];
        mSide[i] = mTop[j];
        mTop[j] = tmp;
    }

    private Pairij findPivot(E[][] array)
    {
        List<Pairij> allPossible = findAllPossiblePivots(array);
        if (allPossible.size() > 0)
        {
            return allPossible.get(0);
        }
        else
        {
            return null;
        }
    }

    private List<Pairij> findAllPossiblePivots(E[][] array)
    {
        Pairij retValueIsOne = null;
        List<Pairij> ret = new ArrayList<Pairij>();
        final int height = array.length;
        final int width = array[0].length;
        AllDone:
        for (int i = 0; i < height; i++)
        {
            if (mSide[i].isNumber())
            {
                for (int j = 0; j < width; j++)
                {
                    if (mTop[j].isVariable())
                    {
                        // candidate found:
                        Pairij pair = new Pairij(i, j);
                        if (isValueOne(array[i][j]))
                        {
                            retValueIsOne = pair;
                            break AllDone;
                        }
                        ret.add(pair);
                    }
                }
            }
        }

        // Now make sure value "1" has priority slot #0 ...
        if (retValueIsOne != null)
        {
            ret.clear();
            ret.add(retValueIsOne);
        }

        return ret;
    }


    public static class Pairij
    {
        public final int i;
        public final int j;

        public Pairij(int i2, int j2)
        {
            i = i2;
            j = j2;
        }
        @Override
        public String toString()
        {
            return "Pairij[i=" + i + " j=" + j + "]";
        }
    }

    private void print(NumberOrVariable<E>[] top,
                       E[][] array,
                       NumberOrVariable<E>[] side,
                       PrintStream out)
    {
        if (out == null)
        {
            return;
        }
        final int width = array[0].length;
        final int height = array.length;
        String sep = "";
        for (int j = 0; j < width; j++)
        {
            out.print(sep);
            sep = " ";
            out.print(top[j]);
        }
        out.println("");

        for (int i = 0; i < height; i++)
        {
            sep = "";
            for (int j = 0; j < width; j++)
            {
                out.print(sep);
                sep = " ";
                out.print(array[i][j]);
            }
            out.print(sep);
            out.print(side[i]);
            out.println("");
        }
    }

    @SuppressWarnings("unchecked")
    private NumberOrVariable<E>[] createNumberOrVariable(int size)
    {
        return new NumberOrVariable[size];
    }

    private E[][] fillInArray(E[][] origin, int ignoreThisColumnIndex)
    {
        final int height = origin.length;
        final int width = origin[0].length;
        final int widthForReturn = width - 1;
        //System.out.println("origin.w=" + width + " origin.height=" + height);

        E[][] ret = create(height, widthForReturn);

        for (int i = 0; i < height; i++)
        {
            int targetJindex = 0;
            for (int j = 0; j < width; j++)
            {
                if (j != ignoreThisColumnIndex)
                {
                    ret[i][targetJindex] = origin[i][j];
                    targetJindex++;
                }
                else
                {
                    // skip this column
                }
            }
        }
        //System.out.println("Started at (w=" + width + " h=" + height + ") ended at (w=" + ret[0].length + " h=" + ret.length + ")");
        return ret;
    }



    private NumberOrVariable<E>[] fillInConstants(int height)
    {
        NumberOrVariable<E>[] ret = createNumberOrVariable(height);
        for (int j = 0; j < height; j++)
        {
            ret[j] = new NumberOrVariable<E>(getConstantNumbered(j));
        }
        return ret;
    }

    private NumberOrVariable<E>[] fillInTopVariables(int width)
    {
        NumberOrVariable<E>[] ret = createNumberOrVariable(width);
        for (int i = 0; i < width; i++)
        {
            ret[i] = createVariableForIndex(i);
        }
        return ret;
    }

    private NumberOrVariable<E> createVariableForIndex(int i)
    {
        return new NumberOrVariable<E>(Integer.toHexString(i + 10));
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
        @Override
        public String toString()
        {
            if (isNumber())
            {
                return number.toString();
            }
            else
            {
                return variable;
            }
        }
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((number == null) ? 0 : number.hashCode());
            result = prime * result
                    + ((variable == null) ? 0 : variable.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            NumberOrVariable other = (NumberOrVariable) obj;
            if (number == null)
            {
                if (other.number != null)
                    return false;
            }
            else if (!number.equals(other.number))
                return false;

            if (variable == null)
            {
                if (other.variable != null)
                    return false;
            }
            else if (!variable.equals(other.variable))
                return false;
            return true;
        }
    }


    // Samples for testing
    // 2 -1 1 = 2
    // 1 2 -1 = 3
    // 3 1 2 = -1
    // answer x=2 y=-1 z=-3
    //    Output:  current pivots on [i=0 j=2], [i=1 j=1], [i=2 j=0]   13, 22, 31

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
    //   if r!=i and s==j   a(rj) / a(ij)
    //   if r==i and s!=j   -1*a(is)/ a(ij)
    //   if r==i and s==j    1/a(ij)
    // Bbar = (b1 .. b(i-1) x(j) b(i+1) ... b(m))
    // Xbar = (x1 .. x(j-1) b(i) x(j+1) ... x(n))  // TODO: b(j)?? or b(i)  or x(i) above?
    //
    //   i m r height      j n s width
    //
    // det(c11 c12)
    //    (c21 c22)  = c11*c22 - c21*c12
    private E[][] createAbar(E[][] array, int i, int j)
    {
        E[][] ret = createSameSize(array);
        int height = ret.length;
        int width = ret[0].length;
        for (int r = 0; r < height; r++)
        {
            for (int s = 0; s < width; s++)
            {
                if ((r != i) && (s != j))
                {
                    E det = determinant(array, r, s, i, j);
                    E oneOverAij = reciprocal(array[i][j]);
                    ret[r][s] = multiply(det, oneOverAij);
                }
                else if ((r != i) && (s == j))
                {
                    E oneOverAij = reciprocal(array[i][j]);
                    ret[r][s] = multiply(array[r][j], oneOverAij);
                }
                else if ((r == i) && (s != j))
                {
                    E oneOverAij = reciprocal(array[i][j]);
                    E negAis = negate(array[i][s]);
                    ret[r][s] = multiply(negAis, oneOverAij);
                }
                else if ((r == i) && (s == j))
                {
                    E oneOverAij = reciprocal(array[i][j]);
                    ret[r][s] = oneOverAij;
                }
                else
                {
                    throw new ArithmeticException("Programmer error");
                }
                //System.out.println("h=" + height + " w=" + width + " r=" + r + " s=" + s + " val=" + ret[r][s]);
            }
        }

        return ret;
    }


    private E[][] createSameSize(E[][] array)
    {
        int height = array.length;
        int width = array[0].length;
        E[][] ret = create(height, width);
        //System.out.println("Same size of h=" + height + " w=" + width + " result h=" + ret.length + " w=" + ret[0].length);
        return ret;
    }



    private E[][] create(int height, int width)
    {
        return matrix.create(height, width);
    }

    public boolean isValueOne(E v)
    {
        return matrix.isValueOne(v);
    }

    private E negate(E v)
    {
        return matrix.negate(v);
    }

    private E multiply(E o1, E o2)
    {
        return matrix.multiply(o1, o2);
    }

    private E add(E o1, E o2)
    {
        return matrix.add(o1, o2);
    }

    private E reciprocal(E v)
    {
        return matrix.reciprocal(v);
    }

    private E determinant(E[][] array, int r, int s, int i, int j)
    {
        return matrix.determinant(array, r, s, i, j);
    }

    private E zero()
    {
        return matrix.zero();
    }
}

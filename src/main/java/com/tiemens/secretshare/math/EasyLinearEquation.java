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
package com.tiemens.secretshare.math;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tiemens.secretshare.exceptions.SecretShareException;

/**
 * "Easy" implementation of linear equation solver.
 *
 *
 * Example: given 3 equations like:
 * 1491 =  83a + 32b + 22c
 *  329 =   5a + 13b + 22c
 *  122 =   3a +  2b + 19c
 * The goal is to solve for a, b, and c [3 unknowns, 3 equations].
 *
 * The problem above is encoded into a matrix of numbers like:
 *  1491   83   32   22
 *   329    5   13   22
 *   122    3    2   19
 * and stored in this class as a List[Row] objects.
 *
 * Then, this class can "solve" it into the 'diagonal 1', giving:
 *   11    1    0    0
 *   16    0    1    0
 *    3    0    0    1
 *  Which in turn means that a=11, b=16 and c=3
 *
 *
 * This implementation is called "easy" because it is really straight-forward,
 *   but also really inefficient.
 * Values are "canceled" by multiplying the two together.
 * e.g. "8" and "4" just needs the "4" to be multiplied by 2, then subtracted.
 * This implementation takes 8*4 and subtracts 4*8 because that just works, and you don't
 *   need to compute the least common multiple.
 *
 * This implementation is also "easy" since it doesn't use any lin-eq library.
 *   There are a lot of those libraries available: it turns out it was easier
 *   to write this class than to figure out how to use the horrible APIs they presented.
 *   [The ones with good APIs didn't support BigInteger]
 *
 * @author tiemens
 *
 */
public class EasyLinearEquation
{
    // ==================================================
    // class static data
    // ==================================================
    // want to turn on debug?  See EasyLinearEquationTest.enableLogging()
    private static Logger logger = Logger.getLogger(EasyLinearEquation.class.getName());

    // ==================================================
    // class static methods
    // ==================================================

    public static Logger getLogger()
    {
        return logger;
    }


    public static BigInteger[][] convertIntToBigInteger(int[][] inMatrix)
    {
        BigInteger[][] cvt = new BigInteger[inMatrix.length][];
        for (int i = 0, n = inMatrix.length; i < n; i++)
        {
            cvt[i] = new BigInteger[inMatrix[i].length];
            for (int c = 0, rn = inMatrix[i].length; c < rn; c++)
            {
                cvt[i][c] = BigInteger.valueOf(inMatrix[i][c]);
            }
        }
        return cvt;
    }

    // ==================================================
    // instance data
    // ==================================================
    private final List<Row> rows;

    // 'modulus' can be null, which means do not perform mod() on values
    private final BigInteger modulus;

    // ==================================================
    // factories
    // ==================================================

    /**
     * Create solver for polynomial equations.
     *
     * Polynomial linear equations are a special case, because the C,x,x^2,x^3 coefficients
     *  can be turned into the rows we need by being given:
     *    a) which "x" values were used
     *    b) what "constant" values were computed
     * This information happens to be exactly what the holder of a "Secret" in
     *     "Secret Sharing" has been given.
     *   So this constructor can be used to recover the secret if
     *     given enough of the secrets.
     *
     * @param xarray the "X" values
     * @param fofxarray the "f(x)" values
     * @return instance for solving this special case
     */
    public static EasyLinearEquation createForPolynomial(final BigInteger[] xarray,
                                                         final BigInteger[] fofxarray)
    {
        if (xarray.length != fofxarray.length)
        {
            throw new SecretShareException("Unequal length arrays are not allowed");
        }
        final int numRows = xarray.length;
        final int numCols = xarray.length + 1;

        BigInteger[][] cvt = new BigInteger[numRows][];
        for (int row = 0; row < numRows; row++)
        {
            cvt[row] = new BigInteger[numCols];

            fillInPolynomial(cvt[row],
                             fofxarray[row],
                             xarray[row]);
        }

        return create(cvt);
    }


    /**
     * Convenience factory to create an instance with 'int's instead of BigIntegers.
     *
     * @param inMatrix given in 'int's
     * @return instance
     */
    public static EasyLinearEquation create(int[][] inMatrix)
    {
        BigInteger[][] cvt = convertIntToBigInteger(inMatrix);

        return create(cvt);
    }


    /**
     * Most typical factory, for BigInteger arrays.
     *
     * @param inMatrix given in BigIntegers, where the first column is the constant
     *     and all the other columns are the variables
     * @return instance
     */
    public static EasyLinearEquation create(BigInteger[][] inMatrix)
    {
        EasyLinearEquation ret = null;
        final int width = inMatrix[0].length;
        for (BigInteger[] row : inMatrix)
        {
            if (width != row.length)
            {
                throw new SecretShareException("All rows must be " +
                                               width + " wide");
            }
        }
        List<Row> rows = new ArrayList<Row>();
        for (BigInteger[] row : inMatrix)
        {
            Row add = Row.create(row);
            rows.add(add);
        }
        ret = new EasyLinearEquation(rows);
        return ret;
    }


    // ==================================================
    // constructors
    // ==================================================

    private EasyLinearEquation(final List<Row> inRows)
    {
        this(inRows, null);
    }
    private EasyLinearEquation(final List<Row> inRows,
                               final BigInteger inModulus)
    {
        rows = new ArrayList<Row>();
        rows.addAll(inRows);

        modulus = inModulus;
    }

    public EasyLinearEquation createWithPrimeModulus(BigInteger primeModulus)
    {
        if (primeModulus != null)
        {
            return new EasyLinearEquation(this.rows, primeModulus);
        }
        else
        {
            throw new SecretShareException("modulus cannot be null");
        }
    }


    // ==================================================
    // public methods
    // ==================================================

    public EasySolve solve()
    {
        EasySolve ret = null;

        List<Row> solverows = new ArrayList<Row>();
        solverows.addAll(rows);
        debugRows("Initial rows", solverows, modulus);
        for (int workrowindex = 0, maxindex = solverows.size(); workrowindex < maxindex; workrowindex++)
        {
            Row otherrow = solverows.get(workrowindex);
            for (int fixindex = workrowindex + 1; fixindex < maxindex; fixindex++)
            {
                int columnIndexToCancel = workrowindex + 1;

                Row cancelrowr = solverows.get(fixindex).cancelColumn(columnIndexToCancel,
                                                                      otherrow,
                                                                      modulus);
                solverows.set(fixindex, cancelrowr);
            }
            debugRows("after workrowindex=" + workrowindex + " finished", solverows, modulus);
        }
        debugRows("after all loops", solverows, modulus);
        //
        // the matrix should look like this now:
        //
        // 33  a b c
        // -51 0 d e
        // -13 0 0 f
        // so, start at the bottom, and solve and cancel the other direction:
        for (int workrowindex = solverows.size() - 1; workrowindex >= 0; workrowindex--)
        {
            Row reducedToOne = solverows.get(workrowindex).solveThisRow(modulus);
            logger.fine("reverse, index=" + workrowindex + " is " + reducedToOne.debugRow());
            solverows.set(workrowindex, reducedToOne);
            for (int fixindex = workrowindex - 1; fixindex >= 0; fixindex--)
            {
                int columnIndexToCancel = workrowindex + 1;

                logger.finer("  going to cancel fixindex=" + fixindex + " is " +
                             solverows.get(fixindex).debugRow() + " using row " +
                             reducedToOne.debugRow());
                Row cancelrowr = solverows.get(fixindex).cancelColumn(columnIndexToCancel,
                                                                      reducedToOne,
                                                                      modulus);
                solverows.set(fixindex, cancelrowr);
            }
            debugRows("After reverse loopindex=" + workrowindex + " finished", solverows, modulus);
        }
        //
        // the matrix should look like this now:
        //
        //  3 1 0 0
        // -5 0 1 0
        // -3 0 0 1
        BigInteger[] answers = new BigInteger[solverows.size() + 1];
        answers[0] = null;
        for (int i = 1, n = answers.length; i < n; i++)
        {
            answers[i] = solverows.get(i - 1).getColumn(0);
        }
        ret = new EasySolve(answers);
        return ret;
    }
    private void debugRows(String where,
                           List<Row> solverows,
                           BigInteger modulus)
    {
        // want to turn on debug?  See EasyLinearEquationUT.enableLogging()
        if (logger.isLoggable(Level.FINE))
        {
            logger.fine(where + " (modulus=" + modulus + ")");
            for (Row row : solverows)
            {
                logger.fine(row.debugRow());
            }
        }
    }
    // ==================================================
    // non public methods
    // ==================================================
    public static class EasySolve
    {
        private final BigInteger[] answers;
        public EasySolve(BigInteger[] inAnswers)
        {
            answers = new BigInteger[inAnswers.length];
            System.arraycopy(inAnswers, 0, answers, 0, answers.length);
        }

        public BigInteger getAnswer(int i)
        {
            if (i < 0)
            {
                throw new SecretShareException("Answer index cannot be negative: " + i);
            }
            if (i == 0)
            {
                throw new SecretShareException("Answer index 0 is the constant, not an answer." +
                                               "  Use range 1-n [not 0-n-1]");
            }
            return answers[i];
        }

    }

    private static class Trial
    {
        private final String which;
        private final BigInteger result;
        private final boolean correct;

        public Trial(final String inWhich,
                     final BigInteger inOriginal,
                     final BigInteger inDivideby)
        {
            which = inWhich;
            result = inOriginal.divide(inDivideby);
            correct = result.multiply(inDivideby).equals(inOriginal);
        }

        public BigInteger getResult()
        {
            if (correct)
            {
                return result;
            }
            else
            {
                throw new SecretShareException("Tried to get result from non-correct trial");
            }
        }
        @SuppressWarnings("unused")
        public String dumpDebug()
        {
            return "Trial[" + which + " result=" + result;
        }


        /**
         * Construct all the permutations we need.
         */
        public static List<Trial> createList(final BigInteger original,
                                             final BigInteger divideby,
                                             final BigInteger useModulus)
        {
            List<Trial> list =  new ArrayList<Trial>();

            BigInteger o = original;
            int c = 0;
            Trial trial = new Trial("" + c, o, divideby);
            list.add(trial);
            boolean somethingBroke = false;
            while (! trial.correct)
            {
                c++;
                if (c > 10000)
                {
                    somethingBroke = true;
                    break;
                }
                o = o.add(useModulus);
                trial = new Trial("" + c, o, divideby);
            }
            if (somethingBroke)
            {
                System.out.format("ERROR\noriginal %80s\n" +
                                        "dividedby %80s\n" +
                                        "modulus   %80s\n",
                                        original,
                                        divideby,
                                        useModulus);
            }
//            c = 0;
//            while (! trial.correct)
//            {
//                c++;
//                if (c > 10000)
//                {
//                    throw new SecretShareException("two loop failure");
//                }
//                o = o.subtract(useModulus);
//                trial = new Trial("" + c, o, divideby);
//            }

            list.add(trial);
            return list;
        }
        /**
         * Pick the "best" correct result [if any].
         *
         */
        public static Trial pickSuccess(List<Trial> list)
        {
            if (list.get(list.size() - 1).correct)
            {
                return list.get(list.size() - 1);
            }
            else
            {
                System.out.println("WARN: trial[0] did not succeed.");
                for (Trial ret : list)
                {
                    if (ret.correct)
                    {
                        return ret;
                    }
                }
                throw new SecretShareException("Programmer error, no trial correct, list.size=" + list.size());
            }
        }



        /**
         * Original implementation.  Wrong.
         */
        public static List<Trial> createList2(final BigInteger original,
                                             final BigInteger divideby,
                                             final BigInteger useModulus)
        {
            List<Trial> list =  new ArrayList<Trial>();

            list.add(new Trial("original", original, divideby));
            list.add(new Trial("modoriginal", original.mod(useModulus), divideby));
            list.add(new Trial("moddivide", original, divideby.mod(useModulus)));
            list.add(new Trial("mod both", original.mod(useModulus), divideby.mod(useModulus)));

            BigInteger gcd = original.gcd(divideby);
            if ((gcd != null) &&
                (gcd.compareTo(BigInteger.ONE) > 0))
            {
                BigInteger divO = original.divide(gcd);
                BigInteger divD = divideby.divide(gcd);
                list.add(new Trial("gcd original", divO, divD));
                list.add(new Trial("gcd modoriginal", divO.mod(useModulus), divD));
                list.add(new Trial("gcd moddivide", divO, divD.mod(useModulus)));
                list.add(new Trial("gcd both", divO.mod(useModulus), divD.mod(useModulus)));
            }
            return list;
        }
    }

    private static class Row
    {
        private final BigInteger[] cols;

        public static Row create(BigInteger[] in)
        {
            return new Row(in);
        }
        public Row(Row copy)
        {
            this(copy.cols);
        }
        private Row(BigInteger[] in)
        {
            cols = new BigInteger[in.length];
            System.arraycopy(in, 0, cols, 0, cols.length);
        }
        /**
         * @return row with col[0] non-zero and one-and-only-one other column non-zero,
         *           all the other columns must be ZERO    OR
         *         throw exception
         * @throws SecretShareException if more than 2 columns [the 1st and 1 other] are non-zero
         */
        public Row solveThisRow(final BigInteger useModulus)
        {
            // Determine non-zero column:
            Integer nonZeroColumn = null;
            for (int col = 1, n = cols.length; col < n; col++)
            {
                if (! this.isColumnZero(col))
                {
                    if (nonZeroColumn != null)
                    {
                        logger.severe("Row cannot be solved:\n" + debugRow());
                        throw new SecretShareException("Two columns are non-zero, c=" +
                                                       nonZeroColumn + " and c=" + col);
                    }
                    else
                    {
                        nonZeroColumn = col;
                    }
                }
            }
            if (nonZeroColumn == null)
            {
                throw new SecretShareException("No non-zero column found in row; error");
            }


            Row ret = new Row(this);
            final BigInteger divideby = cols[nonZeroColumn];

            //
            // This is kind of like 'row.divideby()', except:
            // a) we know only 2 cols[] are non-zero
            // b) we absolutely need to make sure the result does not have a remainder,
            //    which means we have to "know" about the modulus sometimes
            //
            for (int col = 0, n = ret.cols.length; col < n; col++)
            {
                if ((col == 0) ||
                    (col == nonZeroColumn))
                {
                    BigInteger original = ret.cols[col];
                    BigInteger result = divideNormallyOrModulus(original, divideby, useModulus);
                           // this doesn't always work:  result = original.divide(divideby);
                    ret.cols[col] = result;
                }
                else
                {
                    // Leave the column alone.  Just do a safety-check:
                    if (! ret.isColumnZero(col))
                    {
                        throw new SecretShareException("Programmer error.  " +
                                                       "Column " + col + " must be zero, " +
                                                       "but instead is " + ret.getColumn(col));
                    }
                }
            }

            return ret;
        }


        /**
         * This _should be_ the implementation.
         * The problem is, it doesn't work.
         *
         */
        @SuppressWarnings("unused")
        private BigInteger divideNormallyOrModulusBroken(final BigInteger original,
                                                         final BigInteger divideby,
                                                         final BigInteger useModulus)
        {
            BigInteger result = null;

            if (useModulus == null)
            {
                result = original.divide(divideby);
            }
            else
            {
                result = original.divide(divideby);

                // do the "math check" before the modulus:
                safetyCheckDivision(result, divideby, original);

                // modfix: not proven to help reduce errors:  mod down the result
                //result = result.mod(useModulus);
            }

            return result;
        }

        /**
         * The modulus stuff is really strange.
         * Sometimes the divide-by just works.
         * Sometimes it is negative but "odd", and needs mod() to positive and "even".
         * Sometimes it is positive but "too big and odd" and needs mod() to a smaller "even" number.
         * Sometimes it needs multiple-add-the-modulus, especially for small values of
         *       the modulus versus large values of the coefficients.
         *
         * This routine just tries a bunch of things, including:
         *     1) original / divideby and
         *     2) (original mod useModulus) / divideby
         *  See class Trial.
         *
         * @param original
         * @param divideby
         * @param useModulus
         * @return
         */
        private BigInteger divideNormallyOrModulus(final BigInteger original,
                                                   final BigInteger divideby,
                                                   final BigInteger useModulus)
        {
            BigInteger result = null;

            if (useModulus == null)
            {
                result = original.divide(divideby);
            }
            else
            {
                // Create all of the trial "divide by" combinations
                List<Trial> list = Trial.createList(original, divideby, useModulus);

                // Pick the "best correct solution"
                Trial success = Trial.pickSuccess(list);

                if (success == null)
                {
                    throw new SecretShareException("All trial divide bys failed");
                }
                else
                {
                    result = success.getResult();
                    result = result.mod(useModulus);
                    //
                    // For big (192-bit) modulus, it is almost always "0"
                    // For small (e.g. 59561) modulus, it ranges from 0 to 20
                    // run "UT.testFirst()"
                    if (debugPrinting)
                    {
                        // Debug printing
                        if (! "0".equals(success.which))
                        {
                        System.out.println("Trial.sucess.which=" + success.which);
                        }
                    }
                }
            }

            // safetyCheckDivision(result, divideby, original);
            return result;
        }
        private final boolean debugPrinting = false;

        private void safetyCheckDivision(BigInteger result,
                                         BigInteger divideby,
                                         BigInteger original)
        {
            if (! result.multiply(divideby).equals(original))
            {
                throw new SecretShareException("division left remainder: original=" +
                                               original + "\nDivided by=" + divideby +
                                               "\nError.");
            }
        }
        private boolean isColumnZero(int index)
        {
            if (this.getColumn(index).compareTo(BigInteger.ZERO) == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        public String debugRow()
        {
            String ret = "";
            String sep = "";
            for (BigInteger c : cols)
            {
                ret += sep;
                sep = ",";
                ret += c.toString();
            }
            return ret;
        }
        public BigInteger getColumn(final int index)
        {
            return cols[index];
        }
        /**
         * @param index of column to cancel (range 1-to-n)
         * @param otherrow to use for the cancel operation
         * @return row with column value set to "0"
         */
        public Row cancelColumn(final int index,
                                final Row otherrow,
                                final BigInteger useModulus)
        {
            // special case: our col[index] is already zero:
            if (this.isColumnZero(index))
            {
                return this;
            }


            boolean samesign = this.sameSign(index, otherrow);
            BigInteger mult = this.getColumn(index);
            if (samesign)
            {
                mult = mult.negate();
            }
            Row cancel = otherrow.multiplyConstant(mult);

            mult = otherrow.getColumn(index);
            Row usethis = this.multiplyConstant(mult);
            if (! samesign)
            {
                usethis = usethis.negate();
            }

            Row ret = null;
            if (! usethis.sameSign(index, cancel))
            {
                ret = usethis.add(cancel);

                if (useModulus != null)
                {
                    if (ret.cols[0].signum() == -1)
                    {
                        // modfix: not proven to reduce errors: mod down the column value
                        //ret.cols[0] = ret.cols[0].mod(useModulus);

                        // modfix: let's try keeping cols[0] positive:
                        usethis = usethis.negate();
                        cancel = cancel.negate();
                        ret = usethis.add(cancel);
                    }
                }
            }
            else
            {
                throw new SecretShareException("prog error this(" + index + ")=" +
                                               this.getColumn(index) +
                                               " other(" + index + ")=" +
                                               cancel.getColumn(index));
            }
            return ret;
        }
        public boolean sameSign(final int index,
                                final Row other)
        {
            return sameSign(this.getColumn(index), other.getColumn(index));
        }
        private boolean sameSign(final BigInteger one,
                                 final BigInteger other)
        {
            int thisc  = one.compareTo(BigInteger.ZERO);
            int otherc = other.compareTo(BigInteger.ZERO);
            // change ZERO into positive
            thisc  = (thisc == 0)  ? 1 : thisc;
            otherc = (otherc == 0) ? 1 : otherc;

            boolean ret = (thisc == otherc);
            logger.finest("  samesign=" + ret + " on one=" + one +
                          " other=" + other);

            return ret;
        }
        public Row multiplyConstant(final BigInteger mult)
        {
            Row ret = new Row(this);

            for (int c = 0, n = cols.length; c < n; c++)
            {
                ret.cols[c] = this.cols[c].multiply(mult);
            }
            return ret;
        }
        @SuppressWarnings("unused")
        public Row addConstant(final BigInteger add)
        {
            Row ret = new Row(this);

            for (int c = 0, n = cols.length; c < n; c++)
            {
                ret.cols[c] = this.cols[c].add(add);
            }
            return ret;
        }
        public Row add(final Row add)
        {
            Row ret = new Row(this);

            for (int c = 0, n = cols.length; c < n; c++)
            {
                ret.cols[c] = this.cols[c].add(add.getColumn(c));
            }
            return ret;
        }

        public Row negate()
        {
            return multiplyConstant(BigInteger.valueOf(-1));
        }
    }



    /**
     *
     * @param array to fill with values
     * @param theconstant the value of the "C"
     * @param x the "x" value used
     */
    private static void fillInPolynomial(BigInteger[] array,
                                         BigInteger theconstant,
                                         BigInteger x)
    {
        // what was f(x) becomes our constant:
        array[0] = theconstant;
        // what was "C" becomes an unknown, with the coefficient "1"
        array[1] = BigInteger.ONE;

        // the other coefficients are x, x^2, x^3, x^4 etc:
        BigInteger current = BigInteger.ONE;
        for (int i = 2, n = array.length; i < n ; i++)
        {
            current = current.multiply(x);
            array[i] = current;
        }
    }




}

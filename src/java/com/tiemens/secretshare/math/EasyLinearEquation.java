package com.tiemens.secretshare.math;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 * and stored in this class as a List<Row> objects.
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
    public static Logger logger = Logger.getLogger(EasyLinearEquation.class.getName());
    
    // ==================================================
    // class static methods
    // ==================================================

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
        rows = new ArrayList<Row>();
        rows.addAll(inRows);
    }
    
    
    // ==================================================
    // public methods
    // ==================================================
    
    public EasySolve solve()
    {
        EasySolve ret = null;
        
        List<Row> solverows = new ArrayList<Row>();
        solverows.addAll(rows);
        debugRows("Initial rows", solverows);
        for (int workrowindex = 0, maxindex = solverows.size(); workrowindex < maxindex; workrowindex++)
        {
            Row otherrow = solverows.get(workrowindex);
            for (int fixindex = workrowindex + 1; fixindex < maxindex; fixindex++)
            {
                int columnIndexToCancel = workrowindex + 1;
                
                Row cancelrowr = solverows.get(fixindex).cancelColumn(columnIndexToCancel, otherrow);
                solverows.set(fixindex, cancelrowr);
            }
            debugRows("after workrowindex=" + workrowindex + " finished", solverows);
        }
        debugRows("after all loops", solverows);
        //
        // the matrix should look like this now:
        //
        // 33  a b c
        // -51 0 d e
        // -13 0 0 f
        // so, start at the bottom, and solve and cancel the other direction:
        for (int workrowindex = solverows.size() - 1; workrowindex >= 0; workrowindex--)
        {
            Row reducedToOne = solverows.get(workrowindex).solveThisRow();
            logger.fine("reverse, index=" + workrowindex + " is " + reducedToOne.debugRow());
            solverows.set(workrowindex, reducedToOne);
            for (int fixindex = workrowindex - 1; fixindex >= 0; fixindex--)
            {
                int columnIndexToCancel = workrowindex + 1;
                
                logger.finer("  going to cancel fixindex=" + fixindex + " is " + 
                             solverows.get(fixindex).debugRow() + " using row " +
                             reducedToOne.debugRow());
                Row cancelrowr = solverows.get(fixindex).cancelColumn(columnIndexToCancel, reducedToOne);
                solverows.set(fixindex, cancelrowr);
            }
            debugRows("After reverse loopindex=" + workrowindex + " finished", solverows);
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
                           List<Row> solverows)
    {
        logger.fine(where);
        for (Row row : solverows)
        {
            logger.fine(row.debugRow());
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
        public Row solveThisRow()
        {
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
            
            //    
            Row ret = new Row(this);
            
            final BigInteger divideby = cols[nonZeroColumn];
            BigInteger[] vals = ret.cols;  // TODO: refactor out this 'alias'
            
            vals[0] = cols[0].divide(divideby);
            // safety check:
            safetyCheckDivision("column0", vals[0], divideby, cols[0]);
            
            for (int col = 1, n = cols.length; col < n; col++)
            {
                if (col == nonZeroColumn)
                {
                    vals[col] = cols[col].divide(divideby);
                    safetyCheckDivision("column" + col, vals[col], divideby, cols[col]);
                }
                else
                {
                    vals[col] = cols[col]; // cols[col] must be ZERO here
                }
            }
            
            return ret;
        }

        private void safetyCheckDivision(String string,
                                         BigInteger result,
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
                                final Row otherrow)
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
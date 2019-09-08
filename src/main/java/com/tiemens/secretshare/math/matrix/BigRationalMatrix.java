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
package com.tiemens.secretshare.math.matrix;

import java.math.BigInteger;

import com.tiemens.secretshare.math.type.BigRational;

public class BigRationalMatrix extends NumberMatrix<BigRational>
{

    protected BigRationalMatrix(BigRational[][] in)
    {
        super(in);
    }

    public BigRationalMatrix(int height, int width)
    {
        super(height, width);
    }

    public static BigRationalMatrix create(BigInteger[][] matrix)
    {
        final int height = matrix.length;
        final int width = matrix[0].length;
        BigRational[][] in = new BigRational[height][width];
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                in[i][j] = new BigRational(matrix[i][j]);
            }
        }
        return new BigRationalMatrix(in);
    }

    @Override
    protected BigRational[][] create(int height, int width)
    {
        return new BigRational[height][width];
    }

    @Override
    protected BigRational zero()
    {
        return BigRational.ZERO;
    }

    @Override
    protected BigRational one()
    {
        return BigRational.ONE;
    }


    @Override
    protected BigRational add(BigRational o1, BigRational o2)
    {
        return o1.add(o2);
    }

    @Override
    protected BigRational subtract(BigRational o1, BigRational o2)
    {
        return o1.subtract(o2);
    }

    @Override
    protected BigRational multiply(BigRational o1, BigRational o2)
    {
        return o1.multiply(o2);
    }

    @Override
    protected BigRational reciprocal(BigRational o1)
    {
        return o1.reciprocal();
    }

    @Override
    protected BigRational negate(BigRational o1)
    {
        return o1.negate();
    }

    @Override
    protected BigRational createValue(int v)
    {
        return new BigRational(v);
    }
}

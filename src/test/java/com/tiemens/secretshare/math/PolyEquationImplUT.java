/*******************************************************************************
 * $Id: $
 * Copyright (c) 2009-2010 Tim Tiemens.
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
package com.tiemens.secretshare.math;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

public class PolyEquationImplUT
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

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    // ==================================================
    // public methods
    // ==================================================

	@Test
    public void testBasic()
    {
        // REMEMBER: arguments are REVERSED, i.e. "432" is the constant:
        PolyEquationImpl poly = PolyEquationImpl.create(432, 13, 5, 8);
        int x = 0;
        subtest(poly, x, BigInteger.valueOf((8*(x*x*x)) + (5*(x*x)) + (13*x) + 432));

        x = 1;
        subtest(poly, x, BigInteger.valueOf((8*(x*x*x)) + (5*(x*x)) + (13*x) + 432));
        x = 2;
        subtest(poly, x, BigInteger.valueOf((8*(x*x*x)) + (5*(x*x)) + (13*x) + 432));

    }

    // ==================================================
    // non public methods
    // ==================================================

    private void subtest(PolyEquationImpl poly,
                         int x,
                         BigInteger expected)
    {
        BigInteger actual = poly.calculateFofX(BigInteger.valueOf(x));
        Assert.assertEquals("test x=" + x, expected, actual);
    }
}

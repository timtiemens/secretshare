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
package com.tiemens.secretshare.engine;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import com.tiemens.secretshare.engine.SecretShare.ParanoidInput;

public class SecretShareParanoidInputTest
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

    // @Before


    // ==================================================
    // public methods
    // ==================================================


    @Test
    public void testJustMax()
    {
        validate("5", 5, null, null);
        validate("50,stopCombiningWhenAnyCount=4,limitPrint=22", 50, 4, 22);
        validate("50,limitPrint=22", 50, null, 22);
        validate("limitPrint=22,50", 50, null, 22);
        validate("maxCombinationsAllowedToTest=50,limitPrint=22", 50, null, 22);
        validate("maxCombinationsAllowedToTest=50,stopCombiningWhenAnyCount=3,limitPrint=22", 50, 3, 22);
    }

    // ==================================================
    // non public methods
    // ==================================================


    private void validate(String arg,
                          Integer expectMaxInt,
                          Integer expectStopWhenCount,
                          Integer expectLimitPrint)
    {
        ParanoidInput actual = ParanoidInput.parseForCombine("paranoid", arg);
        BigInteger expectMax = (expectMaxInt == null) ? null : BigInteger.valueOf(expectMaxInt);

        Assert.assertEquals(expectMax, actual.getMaximumCombinationsToTest());
        Assert.assertEquals(expectStopWhenCount, actual.getStopCombiningWhenAnyCount());
        Assert.assertEquals(expectLimitPrint, actual.getLimitPrint());
    }


}

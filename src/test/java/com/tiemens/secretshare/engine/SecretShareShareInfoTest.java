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

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import com.tiemens.secretshare.engine.SecretShare.PublicInfo;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;

public class SecretShareShareInfoTest
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
    public void testEqualsWhenKisDifferent()
    {
        BigInteger bigintShare = new BigInteger("12345");
        BigInteger modulus = new BigInteger("17");

        PublicInfo publicInfo1 = new PublicInfo(/*n*/ 3, /*k*/ 2, modulus, "public1");
        PublicInfo publicInfo2 = new PublicInfo(/*n*/ 6, /*k*/ 3, modulus, "public1");
        ShareInfo s1 = new ShareInfo(/*x*/ 1, bigintShare, publicInfo1);
        ShareInfo s2 = new ShareInfo(/*x*/ 1, bigintShare, publicInfo2);

        assertNotEquals(s1, s2);
    }

    // ==================================================
    // non public methods
    // ==================================================


}

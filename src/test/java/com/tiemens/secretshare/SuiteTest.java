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
package com.tiemens.secretshare;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.tiemens.secretshare.engine.SecretShareTest;
import com.tiemens.secretshare.math.BigIntStringChecksumTest;
import com.tiemens.secretshare.math.BigIntUtilitiesTest;
import com.tiemens.secretshare.math.EasyLinearEquationTest;
import com.tiemens.secretshare.math.PolyEquationImplTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BigIntStringChecksumTest.class,
    BigIntUtilitiesTest.class,
    PolyEquationImplTest.class,
    EasyLinearEquationTest.class,
    SecretShareTest.class
})
public class SuiteTest
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

    // ==================================================
    // non public methods
    // ==================================================
}

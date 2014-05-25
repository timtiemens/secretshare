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
package com.tiemens.secretshare;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.tiemens.secretshare.engine.SecretShareUT;
import com.tiemens.secretshare.math.BigIntStringChecksumUT;
import com.tiemens.secretshare.math.BigIntUtilitiesUT;
import com.tiemens.secretshare.math.EasyLinearEquationUT;
import com.tiemens.secretshare.math.PolyEquationImplUT;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BigIntStringChecksumUT.class,
    BigIntUtilitiesUT.class,
    PolyEquationImplUT.class,
    EasyLinearEquationUT.class,
    SecretShareUT.class
})
public class SuiteUT
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

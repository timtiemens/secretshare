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

import junit.framework.Test;
import junit.framework.TestSuite;

import com.tiemens.secretshare.engine.SecretShareUT;
import com.tiemens.secretshare.math.BigIntStringChecksumUT;
import com.tiemens.secretshare.math.BigIntUtilitiesUT;
import com.tiemens.secretshare.math.EasyLinearEquationUT;
import com.tiemens.secretshare.math.PolyEquationImplUT;

public class SuiteUT
    extends TestSuite
{
    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================
    public static Test suite()
    {
        return createSuite();
    }
    public static Test createSuite()
    {
        TestSuite ret =
            new TestSuite(new SuiteUT().getClass().getName());
        
        ret.addTestSuite(BigIntStringChecksumUT.class);
        ret.addTestSuite(BigIntUtilitiesUT.class);
        ret.addTestSuite(PolyEquationImplUT.class);
        ret.addTestSuite(EasyLinearEquationUT.class);
        ret.addTestSuite(SecretShareUT.class);
        
        return ret;
    }
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

    public static void main(final String[] args)
    {
        junit.textui.TestRunner.run(createSuite());
    }
    // ==================================================
    // non public methods
    // ==================================================
}

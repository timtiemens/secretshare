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
package com.tiemens.secretshare.jdeps.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.tiemens.secretshare.jdeps.model.JDeps.JdepsLineModule2Module;
import com.tiemens.secretshare.jdeps.model.JDeps.JdepsLinePackage2PackageModule;

public class JDepsTest
{

    @Test
    public void testm2m()
    {
        subtestm2m("classes -> java.base", "classes", "java.base");
        subtestm2m("classes -> not found", "classes", "not found");
        subtestm2m(" classes -> java.base", null, null);
    }

    private void subtestm2m(String input, String expectedUser, String expectedUsed)
    {
        JDeps.ParseJdepsLine parser = new JDeps.ParseJdepsLineModule2Module();
        JdepsLineModule2Module ret = (JdepsLineModule2Module) parser.parseLine(input);

        if (expectedUser != null)
        {
            assertNotNull(ret);
            assertEquals(expectedUser, ret.getUserModule());
            assertEquals(expectedUsed, ret.getUsesModule());
        }
        else
        {
            assertNull(ret);
        }
    }

    @Test
    public void testp2pm()
    {
        subtestp2pm("   com.tiemens.secretshare                            -> java.io                                            java.base",
                    "com.tiemens.secretshare", "java.io", "java.base");
    }

    private void subtestp2pm(String input,
            String expectedPackage, String expectedUsesPackage, String expectedUsesModule)
    {
        JDeps.ParseJdepsLine parser = new JDeps.ParseJdepsLinePackage2PackageModule();
        JdepsLinePackage2PackageModule ret = (JdepsLinePackage2PackageModule) parser.parseLine(input);

        if (expectedPackage != null)
        {
            assertNotNull(ret);
            assertEquals(expectedPackage, ret.getUserPackage());
            assertEquals(expectedUsesPackage, ret.getUsesPackage());
            assertEquals(expectedUsesModule, ret.getUsesModule());
        }
        else
        {
            assertNull(ret);
        }
    }
}

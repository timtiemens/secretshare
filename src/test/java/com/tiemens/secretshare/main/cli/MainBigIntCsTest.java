/*******************************************************************************
 * Copyright (c) 2009, 2017Tim Tiemens.
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
package com.tiemens.secretshare.main.cli;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.tiemens.secretshare.main.cli.MainBigIntCs.Type;

public class MainBigIntCsTest
{

    @Test
    public void testConvert()
    {
        subTestConvert("Cat", Type.s, Type.s, "Cat");
        subTestConvert("Cat", Type.s, Type.bi, "4415860");
        subTestConvert("Cat", Type.s, Type.bics, "bigintcs:436174-7BF975");

        subTestConvert("4415860", Type.bi, Type.s, "Cat");
        subTestConvert("4415860", Type.bi, Type.bi, "4415860");
        subTestConvert("4415860", Type.bi, Type.bics, "bigintcs:436174-7BF975");

        subTestConvert("bigintcs:436174-7BF975", Type.bics, Type.s, "Cat");
        subTestConvert("bigintcs:436174-7BF975", Type.bics, Type.bi, "4415860");
        subTestConvert("bigintcs:436174-7BF975", Type.bics, Type.bics, "bigintcs:436174-7BF975");
    }

    @Test
    public void testConvertPattern()
    {
        String asString = "Cat";
        String asBigInt = "4415860";
        String asBigIntCs = "bigintcs:436174-7BF975";
        subTestUsingPattern(asString, asBigInt, asBigIntCs);
    }

    @Test
    public void testConvertPatternLarger()
    {
        // HOWTO: Integer-to-Hex on linux systems:
        // $ echo "obase=16; 1366642514502075287546428215812468" | bc

        // the HOWTO: String-to-Integer is more complicated, since it involves
        //  using the actual BYTES of the string, and interpreting them as a number.

        subTestUsingPattern("Cat In The Hat",
                            "1366642514502075287546428215812468",
                            "bigintcs:004361-742049-6e2054-686520-486174-3633A1");
    }

    private void subTestUsingPattern(String asString, String asBigInt, String asBigIntCs)
    {
        subTestConvert(asString, Type.s, Type.s,    asString);
        subTestConvert(asString, Type.s, Type.bi,   asBigInt);
        subTestConvert(asString, Type.s, Type.bics, asBigIntCs);

        subTestConvert(asBigInt, Type.bi, Type.s,    asString);
        subTestConvert(asBigInt, Type.bi, Type.bi,   asBigInt);
        subTestConvert(asBigInt, Type.bi, Type.bics, asBigIntCs);

        subTestConvert(asBigIntCs, Type.bics, Type.s,    asString);
        subTestConvert(asBigIntCs, Type.bics, Type.bi,   asBigInt);
        subTestConvert(asBigIntCs, Type.bics, Type.bics, asBigIntCs);
    }

    private void subTestConvert(String input, Type inType, Type outType, String expected)
    {
        // Test the low-level routine:
        String actual = MainBigIntCs.BigIntCsOutput.convert(input, inType, outType);
        Assert.assertEquals(expected, actual);

        // Test the command-line args[] interface:
        String mode = inType.toString() + "2" + outType.toString();
        MainReadmeTest.TestOutput testOutput = new MainReadmeTest.TestOutput();
        List<String> args = new ArrayList<String>();
        args.add("bigintcs");
        args.add("-mode");
        args.add(mode);
        args.add(input);
        Main.main(args.toArray(new String[0]), System.in, testOutput.out(), false);
        //System.out.println("out.size=" + testOutput.getLines().size());
        //System.out.println("out.(0)=" + testOutput.getLines().get(0));
        Assert.assertEquals("size wrong", 1, testOutput.getLines().size());
        Assert.assertEquals("expected wrong", expected, testOutput.getLines().get(0));

        // second command-line args test
        args = new ArrayList<String>();
        testOutput = new MainReadmeTest.TestOutput();
        args.add("bigintcs");
        args.add("-in");
        args.add(inType.toString());
        args.add("-out");
        args.add(outType.toString());
        args.add(input);
        Main.main(args.toArray(new String[0]), System.in, testOutput.out(), false);
        Assert.assertEquals("2nd size wrong", 1, testOutput.getLines().size());
        Assert.assertEquals("2nd expected wrong", expected, testOutput.getLines().get(0));

    }

}

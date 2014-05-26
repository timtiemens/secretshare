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
package com.tiemens.secretshare.main.cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The purpose of these tests is to run the "Readme.txt" instructions -
 *     and -generate- that section of the file
 *     to make sure the "first experience" works correctly.
 *
 * @author tiemens
 *
 */
public class MainReadmeTest {

    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass()
            throws Exception
    {
    }

    @Before
    public void setUp()
            throws Exception
    {
    }

    @After
    public void tearDown()
            throws Exception
    {
    }

    @Test
    public void test()
    {
        TestCollector collect = new TestCollector();

        testSimpleCat(collect);
        testSimpleCatPipe(collect);

        testCatInTheHat(collect);
        testCatInTheHatPipe(collect);

        testSimpleCat4096(collect);

        System.out.println("4. Example command line");
        System.out.println("");
        collect.output(System.out);

    }

    private final String smallPrime = "16639793";

    public void testSimpleCat(TestCollector collect)
    {
        //$ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat"
        String[] args = {"split", "-k", "3", "-n", "6", "-m", smallPrime, "-sS", "Cat"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "[creates a share size 6 with threshold 3 with \"Cat\" as the secret string.",
            " Note: the low modulus of " + smallPrime + " limits the size of the secret number,",
            "       which in turn limits the length of the secret string.]"});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(args, input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertSee("n = 6", output);
        assertSee("k = 3", output);
        assertSee("modulus = " + smallPrime, output);
    }

    public void testSimpleCatPipe(TestCollector collect)
    {
        //$ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat" \
        //      | java -jar secretshare.jar combine -stdin
        String[] args = {"split", "-k", "3", "-n", "6", "-m", smallPrime, "-sS", "Cat"};
        String[] args2 = {"combine", "-stdin"};
        collect.firstCommand(args);
        collect.nextCommand(args2);
        collect.comments(new String [] {
               "[runs the same command as above, but pipes that output into the 'combine'",
               " program, which then re-creates the secret and the secret string \"Cat\".]"});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(args, input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertSee("n = 6", output);
        assertSee("k = 3", output);

        // PIPE

        TestInput input2 = output.asTestInput();
        TestOutput output2 = new TestOutput();
        Main.main(args2, input2.in(), output2.out(), false);
        assertSee("secret.string = 'Cat'", output2);
    }


    public void testCatInTheHat(TestCollector collect)
    {
        // $ java -jar secretshare.jar split -k 3 -n 6     -sS "The Cat In The Hat"
        // [creates a share size 6 with threshold 3 with the longer secret string.
        //   Note: no modulus was given, so a pre-defined 384-bit prime was used,
        //     which allows 48 characters of secret string.]
        String[] args = {"split", "-k", "3", "-n", "6", "-sS", "The Cat In The Hat"};
        collect.firstCommand(args);
        collect.comments(new String [] {
               "[creates a share size 6 with threshold 3 with the longer secret string.",
               " Note: no modulus was given, so a pre-defined 384-bit prime was used,",
               "       which allows 48 characters of secret string.]"});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(args, input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertSee("n = 6", output);
        assertSee("k = 3", output);
        assertContains("modulus = ", output);
        assertContains("Share (x:1) = ", output);
    }


    public void testCatInTheHatPipe(TestCollector collect)
    {
        // $ java -jar secretshare.jar split -k 3 -n 6     -sS "The Cat In The Hat" \
        //     | java -jar secretshare.jar combine -stdin
        String[] args = {"split", "-k", "3", "-n", "6", "-sS", "The Cat In The Hat"};
        String[] args2 = {"combine", "-stdin"};
        collect.firstCommand(args);
        collect.nextCommand(args2);
        collect.comments(new String [] {
                     "[creates the same share as above, then pipes the output of 'split'",
                     " into 'combine', and prints out the secret string.]"});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(args, input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertSee("n = 6", output);
        assertSee("k = 3", output);

        // PIPE

        TestInput input2 = output.asTestInput();
        TestOutput output2 = new TestOutput();
        Main.main(args2, input2.in(), output2.out(), false);
        assertSee("secret.string = 'The Cat In The Hat'", output2);
    }


    public void testSimpleCat4096(TestCollector collect)
    {
        // $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat 4096bits"  -prime4096
        String[] args = {"split", "-k", "3", "-n", "6", "-sS", "The Cat In The Hat 4096bits", "-prime4096"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "[creates a share size 6 with threshold 3 with the secret string.",
            " Note: the modulus was given as \"prime4096\", so the pre-defined 40964-bit prime",
            "       was used, which allows 512 characters of secret string.]"});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(args, input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertSee("n = 6", output);
        assertSee("k = 3", output);
        assertContains("modulus = " + "167102221" /*...*/, output);
    }


    private void assertSee(String mustFind, TestOutput output)
    {
        boolean seen = false;
        for (String line : output.getLines())
        {
            if (mustFind.equals(line))
            {
                seen = true;
            }
        }
        if (!seen)
        {
            System.out.println("Number of lines in output: " + output.getLines().size());
            for (String line : output.getLines())
            {
                System.out.println("LINE: '" + line + "'");
            }
        }
        Assert.assertTrue("output failed to find exactly '" + mustFind + "'", seen);
    }
    private void assertContains(String partialMatch, TestOutput output)
    {
        boolean seen = false;
        for (String line : output.getLines())
        {
            if (line.indexOf(partialMatch) >= 0)
            {
                seen = true;
            }
        }
        if (!seen)
        {
            System.out.println("Number of lines in output: " + output.getLines().size());
            for (String line : output.getLines())
            {
                System.out.println("LINE: '" + line + "'");
            }
        }
        Assert.assertTrue("output failed to contain '" + partialMatch + "'", seen);
    }


    public static class TestInput
    {
        private final InputStream is;

        public static TestInput create(List<String> lines) {
            String inputAsString = "";
            String sep = "";
            for (String line : lines)
            {
                inputAsString += line;
                inputAsString += sep;
                sep = "\n";
            }
            return new TestInput(inputAsString);
        }

        public TestInput()
        {
            this("");
        }
        public TestInput(String string)
        {
            is = new ByteArrayInputStream(string.getBytes());
        }

        public InputStream in() {
            return is;
        }

    }

    public static class TestOutput
    {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final PrintStream ps = new PrintStream(baos);

        public PrintStream out() {
            return ps;
        }

        public TestInput asTestInput() {
            return TestInput.create(getLines());
        }

        public List<String> getLines()
        {
            ps.flush();
            String all = baos.toString();
            String[] lines = all.split("\n");
            return Arrays.asList(lines);
        }
    }

    public static class TestCollector
    {
        private final List<TestCollectorSingle> items = new ArrayList<TestCollectorSingle>();
        private TestCollectorSingle current;

        public void output(PrintStream out)
        {
            String sep = "";
            for (TestCollectorSingle item : items)
            {
                out.print(sep);
                sep = "\n";
                item.output(out);
            }
        }
        private void assignCurrent()
        {
            if (current == null)
            {
                current = new TestCollectorSingle();
            }
        }
        public void nextCommand(String[] args)
        {
            assignCurrent();
            current.nextCommand(args);
        }

        public void comments(String[] strings) {
            assignCurrent();
            current.comments(strings);
        }

        public void firstCommand(String[] args) {
            assignCurrent();
            current.firstCommand(args);
        }

        public void finishItem()
        {
            items.add(current);
            current = null;
        }
    }
    public static class TestCollectorSingle
    {
        private final List<String> commands = new ArrayList<String>();
        private final List<String> comments = new ArrayList<String>();

        public void output(PrintStream out)
        {
            String sep = "";
            for (int i = 0, n = commands.size(); i < n; i++)
            {
                String indent;
                String prefix = "";
                String s = commands.get(i);
                if (i != 0)
                {
                    s = "   | " + s;
                    indent = "    ";
                }
                else
                {
                    prefix = "$ ";
                    indent = "  ";
                }
                if ((i + 1) != n)
                {
                    s = s + " \\";
                }

                out.println(indent + prefix + s);
            }
            String indent = "    ";
            for (String s : comments)
            {
                out.println(indent + s);
            }
        }

        public void firstCommand(String[] args)
        {
            addCommand(args);
        }

        public void nextCommand(String[] args)
        {
            addCommand(args);
        }

        public void comments(String[] strings)
        {
            for (String s : strings)
            {
                comments.add(s);
            }
        }

        private void addCommand(String[] args)
        {
            addCommand("java -jar secretshare.jar" + args2string(args));
        }
        private void addCommand(String string) {
            commands.add(string);
        }

        private Object args2string(String[] args) {
            String ret = "";
            String sep = " ";
            for (int i = 0, n = args.length; i < n; i++)
            {
                String a = args[i];
                ret += sep;
                sep = " ";
                boolean needsQuotes = a.indexOf(" ") >= 0;
                needsQuotes = needsQuotes ||
                        (i > 0) ? args[i - 1].equals("-sS") : false;
                if (needsQuotes)
                {
                    ret += "\"";
                }
                ret += a;
                if (needsQuotes)
                {
                    ret += "\"";
                }
            }
            return ret;
        }



    }
}

/*******************************************************************************
 * Copyright (c) 2009, 2014, 2017 Tim Tiemens.
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

import com.tiemens.secretshare.BuildVersion;

/**
 * The purpose of these tests is to run the "Readme.txt" instructions -
 *     and -generate- that section of the file
 *     to make sure the "first experience" works correctly.
 *
 * @author tiemens
 *
 */
public class MainReadmeTest
{

    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception
    {
        BuildVersion.disableFailureInLoad();
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

        testPrintIndiv(collect);
        testCombineThreeExplicit(collect);

        // new "paranoid" option on combine:
        testCombineThreeExplicitShare3With1Wrong(collect);
        testCombineThreeExplicitShareShowingArguments(collect);

        testInfo(collect);

        System.out.println("Examples of command line invocations");
        System.out.println("-----");
        System.out.println("");
        collect.output(System.out);

    }

    private final static String NEWLINE = "-n-";
    private final String smallPrime = "16639793";

    public void testSimpleCat(TestCollector collect)
    {
        //$ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat"
        String[] args = {"split", "-k", "3", "-n", "6", "-m", smallPrime, "-sS", "Cat"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "Create a share size 6 with threshold 3 with \"Cat\" as the secret string.",
            "   Note: the low modulus of " + smallPrime + " limits the size of the secret number,",
            " which in turn limits the length of the secret string."});
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
               "Create a share size 6 with threshold 3 as above, but pipes the output of \"split\" to the input of \"combine\",",
               " which then re-creates the secret number and the secret string \"Cat\"."});
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
               "Create a share size 6 with threshold 3 as above, but with a long secret string.",
               "  Note: no modulus was given, so a pre-defined 384-bit prime was used",
               " as the modulus.  The 384 bit prime allows 48 characters of secret string."});
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
                     "Create the same share as above, then pipes the output of \"split\"",
                     " into the input of \"combine\", which prints out the secret string."});
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
        String[] args = {"split", "-k", "3", "-n", "6", "-prime4096", NEWLINE, "-sS", "The Cat In The Hat 4096bits"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "Create the same share as above, but use a pre-defined 4096 bit prime modulus.",
            "  The 4096 bit prime allows 512 characters of secret string."});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(filterOutNewlines(args), input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertSee("n = 6", output);
        assertSee("k = 3", output);
        assertContains("modulus = " + "167102221" /*...*/, output);
    }

    public void testPrintIndiv(TestCollector collect)
    {
        // $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat 4096bits"  -prime4096 -printIndiv
        String[] args = {"split", "-k", "3", "-n", "6", "-prime4096", NEWLINE, "-sS", "The Cat In The Hat 4096bits", "-printIndiv"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "Create the same share as above, but output in a manner better suited for physically splitting up the shares",
            " in order to give them out individually with all required information."});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(filterOutNewlines(args), input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertSee("n = 6", output);
        assertSee("k = 3", output);
        assertContains("modulus = " + "167102221" /*...*/, output);
    }

    public void testCombineThreeExplicit(TestCollector collect)
    {
        // java -jar secretshare.jar combine -k 3  -prime384 \
        //   -s2 1882356874773438980155973947620693982153929916 \
        //   -s4 1882357204724127580025723830249209987221192644 \
        //   -s5 1882357444072759374568880025530775541595539408
        String[] args = {"combine", "-k", "3", "-prime384", NEWLINE,
                "-s2", "1882356874773438980155973947620693982153929916", NEWLINE,
                "-s4", "1882357204724127580025723830249209987221192644", NEWLINE,
                "-s5", "1882357444072759374568880025530775541595539408"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "Combine 3 shares to recreate the original secret.",
            "  Note: it is important that the -prime argument is specified before -s arguments."});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(filterOutNewlines(args), input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertContains("secret.string = " + "'TheKeyUsedToEncrypt'" /*...*/, output);
    }

    public void testCombineThreeExplicitShare3With1Wrong(TestCollector collect)
    {
        // Note that "-s3" has been destroyed and is now 12345678912...
        // There are 4 possible subsets of the shares - only ONE share has the correct reconstructed secret
        String[] args = {"combine", "-k", "3", "-prime384", NEWLINE,
                "-paranoid", "4", NEWLINE,
                "-s2", "1882356874773438980155973947620693982153929916", NEWLINE,
                "-s3", "12345678912345678912345678912345678",            NEWLINE,
                "-s4", "1882357204724127580025723830249209987221192644", NEWLINE,
                "-s5", "1882357444072759374568880025530775541595539408"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "Combine 4 shares, 3 good and 1 bad, using paranoid combination option."});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(filterOutNewlines(args), input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertContains("combine.4 = x1 = 1882356743151517032574974075571664781995241588 - (validUTF8=true) = 'TheKeyUsedToEncrypt'", output);
        for (String s : output.getLines())
        {
            System.out.println(s);
        }
    }

    public void testCombineThreeExplicitShareShowingArguments(TestCollector collect)
    {
        String[] args = {"combine", "-k", "3", "-m", smallPrime, NEWLINE,
                "-paranoid", "110,limitPrint=4,stopCombiningWhenAnyCount=30", NEWLINE,
                "-s1", "123456", "-s5", "48382",  "-s2", "32223", "-s3", "392933", NEWLINE,
                "-s4", "923334", "-s6", "123122", "-s7", "939444", "-s8", "838333", NEWLINE,
                "-s9", "453322",  "-s10", "499222"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "Combine shares, showing examples for the -paranoid argument.  ",
            " Control how many extra combines to run (110), how many to print (4), and stop when an answer ",
            " has been seen at least this many times (30).  ",
            "Use the -paranoid option if you (1) have extra shares and (2) some of your shares are corrupt."});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(filterOutNewlines(args), input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertContains("paranoid.summary = Disagreement (110 different answers)", output);
        for (String s : output.getLines())
        {
            System.out.println(s);
        }


    }

    public void testInfo(TestCollector collect)
    {
        // Note that "-s3" has been destroyed and is now 12345678912...
        // There are 4 possible subsets of the shares - only ONE share has the correct reconstructed secret
        String[] args = {"info"};
        collect.firstCommand(args);
        collect.comments(new String [] {
            "Print information about Secret Share, including version, 192 bit, 384 bit and 4096 bit primes."});
        collect.finishItem();


        TestInput input = new TestInput();
        TestOutput output = new TestOutput();
        Main.main(filterOutNewlines(args), input.in(), output.out(), false);
        Assert.assertTrue("output has lines", output.getLines().size() > 0);
        assertContains("Modulus 192 bits = 14976407493557531125525728362448106789840013430353915016137", output);
        for (String s : output.getLines())
        {
            System.out.println(s);
        }
    }

    private String[] filterOutNewlines(String[] args)
    {
        List<String> ret = new ArrayList<String>();
        for (String arg : args)
        {
            if (NEWLINE.equals(arg))
            {
                // skip
            }
            else
            {
                ret.add(arg);
            }
        }
        return ret.toArray(new String[0]);
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

        public static TestInput create(List<String> lines)
        {
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

        public InputStream in()
        {
            return is;
        }

    }

    public static class TestOutput
    {
        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private final PrintStream ps = new PrintStream(baos);

        public PrintStream out()
        {
            return ps;
        }

        public TestInput asTestInput()
        {
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
            for (int i = 0, n = items.size(); i < n; i++)
            {
                TestCollectorSingle item = items.get(i);
                out.print(sep);
                sep = "\n";
                item.output(i, out);
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

        public void comments(String[] strings)
        {
            assignCurrent();
            current.comments(strings);
        }

        public void firstCommand(String[] args)
        {
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

        public void output(int index, PrintStream out)
        {
            outputDescription(index, out);
            outputCommandLine(out);
        }
        private void outputDescription(int index, PrintStream out)
        {
            String indent = "  ";
            String prefix  = "" + (char)(index  + 'a') + ". ";
            for (String s : comments)
            {
                out.print(indent + prefix + s);
                prefix = "";
                indent = "";

            }
            out.println("");
        }

        private void outputCommandLine(PrintStream out)
        {
            final String indent = "  ";
            String extraIndent = "";

            out.println(indent + "```");
            // "commands" are things that need a "|" operator between them (and thus "\" followed by a newline)
            //   each command can have a NEWLINE marker, which needs a "\" and newline, but no "|"
            for (int i = 0, n = commands.size(); i < n; i++)
            {
                String prefix = "";
                String onetimeCommandMarker = "$ ";  // or "| "    or   ""
                String backslash = "";               // or "\"

                if (i != 0)
                {
                    onetimeCommandMarker = "| ";
                    extraIndent = " ";
                }
                else
                {
                    onetimeCommandMarker = "$ ";
                    extraIndent = "";
                }
                if ((i + 1) != n)
                {
                    backslash = " \\";
                }
                else
                {
                    backslash = "";
                }



                String s = commands.get(i);
                String[] lines = s.split(NEWLINE);
                if (lines.length > 1)
                {
                    backslash = " \\";
                }
                for (int j = 0, jn = lines.length; j < jn; j++)
                {
                    if ((j + 1) == jn)
                    {
                        if ((i + 1) == n)
                        {
                            backslash = "";
                        }
                    }

                    String line = lines[j];
                    out.println(indent + extraIndent + prefix + onetimeCommandMarker + line + backslash);
                    onetimeCommandMarker = "";
                    backslash = " \\";

                    //System.out.println("i=" + i + " j=" + j + " n=" + n + " jn=" + jn + "  :: backslash=" + backslash);

                    prefix = "   ";
                }
            }
            out.println(indent + "```");
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
        private void addCommand(String string)
        {
            commands.add(string);
        }

        private String args2string(String[] args)
        {
            String ret = "";
            String sep = " ";
            for (int i = 0, n = args.length; i < n; i++)
            {
                final String a = args[i];
                if (NEWLINE.equals(a))
                {
                    ret += NEWLINE;
                }
                else
                {
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
            }
            return ret;
        }
    }
}

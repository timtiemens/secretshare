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

import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntUtilities;

/**
 * Main command line for the "bigintcs" utilities - converting to/from bigintcs, bigint, and String.
 *
 * Takes a mode (bics2bi, bics2s, bi2s, bi2bics, s2bics, s2bi)
 *  and a list of input strings
 *  and writes the conversion strings.
 *
 * @author tiemens
 *
 */
public final class MainBigIntCs
{

    /**
     * @param args from command line
     */
    public static void main(String[] args)
    {
        main(args, System.in, System.out);
    }

    public static void main(String[] args,
                            InputStream in,
                            PrintStream out)
    {
        try
        {
            BigIntCsInput input = BigIntCsInput.parse(args);
            BigIntCsOutput output = input.output();
            output.print(out);
        }
        catch (SecretShareException e)
        {
            out.println(e.getMessage());
            usage(out);
            optionallyPrintStackTrace(args, e, out);
        }
    }

    public static void usage(PrintStream out)
    {
        out.println("Usage:");
        out.println(" bigintcs -mode <bics2bi|bics2s|bi2s|bi2bics|s2bics|s2bi> " +
                    "  [-v] [-in <bics|bi|s>] [-out <bics|bi|s>] [-sepSpace|-sepNewline] value [value2 ...]");
        out.println("  -mode <m>     set operation mode");
        out.println("     m=s        String, converted to bytes, then printed as a number");
        out.println("     m=bi       String, converted to Big Integer, then printed as a number");
        out.println("     m=bics     String, parsed and checksummed to Big Integer Checksum, then printed as a number");
        out.println("  -v            print version on 1st line");
        out.println("  -sepSpace     outputs with spaces between values");
        out.println("  -sepNewLine   outputs with newlines between values");
        out.println("  Note: s(tring) 'Cat' = 4415860");
        out.println("  Note: s(tring) '123' = 3224115  (NOT 123)");
        out.println("  Note: s(tring) 'a'   = 97 (ascii 'a')");
        out.println("  Note: s(tring) 'ab'  = 24930 (ascii 'a' * 256 + ascii)");
        out.println("  Note: bi       '123' = 123");
    }



    public static BigInteger parseBigInteger(String argname,
                                             String[] args,
                                             int index)
    {
        return MainSplit.parseBigInteger(argname, args, index);
    }

    public static Integer parseInt(String argname,
                                   String[] args,
                                   int index)
    {
        return MainSplit.parseInt(argname, args, index);
    }


    public static void checkIndex(String argname,
                                  String[] args,
                                  int index)
    {
        MainSplit.checkIndex(argname, args, index);
    }

    public static void optionallyPrintStackTrace(String[] args,
                                                 Exception e,
                                                 PrintStream out)
    {
        MainSplit.optionallyPrintStackTrace(args, e, out);
    }

    private MainBigIntCs()
    {
        // no instances
    }

    public static enum Type
    {
        bics, bi, s;

        // use 'valueOf(String)' for lookups
        public static Type findByString(String in, String argName)
        {
            Type ret = valueOf(in);
            if (ret != null)
            {
                return ret;
            }
            else
            {
                throw new SecretShareException("Type value '" + in + "' not found." +
                                               ((argName != null) ? "  Argname=" + argName : ""));
            }
        }
    }

    public static enum Type2Type
    {
        bics2bics, bics2bi, bics2s,
        bi2bics,   bi2bi,   bi2s,
        s2bics,    s2bi,    s2s;

        // use 'valueOf(String)' for lookups
        public static Type2Type findByString(String in, String argName)
        {
            Type2Type ret = valueOf(in);
            if (ret != null)
            {
                return ret;
            }
            else
            {
                throw new SecretShareException("Type2Type value '" + in + "' not found." +
                                               ((argName != null) ? "  Argname=" + argName : ""));
            }
        }
        public Type getInputType(String argName)
        {
            String name = this.name();
            return Type.findByString(name.substring(0, name.indexOf('2')), argName);
        }
        public Type getOutputType(String argName)
        {
            String name = this.name();
            return Type.findByString(name.substring(name.indexOf('2') + 1, name.length()), argName);
        }
    }

    public static class BigIntCsInput
    {
    	private static final String systemLineSeparator =
    			System.getProperty("line.separator"); // jdk1.7: System.lineSeparator();

        // ==================================================
        // instance data
        // ==================================================

        // required arguments:
        private final List<String> inputs = new ArrayList<String>();
        private Type inputType = Type.s;
        private Type outputType = Type.bics;

        // optional
        private final boolean printHeader = false;
        private String separator = systemLineSeparator;

        // ==================================================
        // constructors
        // ==================================================
        public static BigIntCsInput parse(String[] args)
        {
            BigIntCsInput ret = new BigIntCsInput();

            for (int i = 0, n = args.length; i < n; i++)
            {
                if (args[i] == null)
                {
                    continue;
                }

                if ("-in".equals(args[i]))
                {
                    i++;
                    ret.inputType = parseType("in", args, i);
                }
                else if ("-out".equals(args[i]))
                {
                    i++;
                    ret.outputType = parseType("out", args, i);
                }
                else if ("-mode".equals(args[i]))
                {
                    i++;
                    Type2Type t2t = parseType2Type("mode", args, i);
                    ret.inputType = t2t.getInputType("mode");
                    ret.outputType = t2t.getOutputType("mode");
                }
                else if ("-sep".equals(args[i]))
                {
                    i++;
                    checkIndex("sep", args, i);
                    ret.separator = args[i];
                }
                else if ("-sepSpace".equals(args[i]))
                {
                    ret.separator = " ";
                }
                else if ("-sepNewLine".equals(args[i]))
                {
                    ret.separator = systemLineSeparator;
                }
                else if (args[i].startsWith("-"))
                {
                    String m = "Argument '" + args[i] + "' not understood";
                    throw new SecretShareException(m);
                }
                else
                {
                    String v = args[i];
                    ret.inputs.add(v);
                }
            }
            return ret;
        }
        public static Type2Type parseType2Type(String argname, String[] args, int index)
        {
            checkIndex(argname, args, index);
            return parseType2Type(argname, args[index]);
        }
        public static Type2Type parseType2Type(String argname, String value)
        {
            return Type2Type.findByString(value, argname);
        }
        public static Type parseType(String argname, String[] args, int index)
        {
            checkIndex(argname, args, index);
            return parseType(argname, args[index]);
        }
        public static Type parseType(String argname, String value)
        {
            return Type.findByString(value, argname);
        }

        // ==================================================
        // public methods
        // ==================================================

        public BigIntCsOutput output()
        {
            BigIntCsOutput ret = new BigIntCsOutput(this);

            return ret;
        }


        // ==================================================
        // non public methods
        // ==================================================
    }

    public static class BigIntCsOutput
    {
        private final BigIntCsInput bigintcsInput;
        private List<String> output;

        public BigIntCsOutput(BigIntCsInput inBigIntCsInput)
        {
            bigintcsInput = inBigIntCsInput;
        }

        public void print(PrintStream out)
        {
            output = convert(bigintcsInput.inputs, bigintcsInput.inputType, bigintcsInput.outputType);
            if (bigintcsInput.printHeader)
            {
                printHeaderInfo(out);
            }
            String sep = "";
            if (output.size() > 0)
            {
                for (String s : output)
                {
                    out.print(sep);
                    sep = bigintcsInput.separator;
                    out.print(s);
                }
                out.print(sep);
            }
        }

        // ==================================================
        // instance data
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

        public static List<String> convert(List<String> inputs,
                                           Type inputType,
                                           Type outputType)
        {
            List<String> ret = new ArrayList<String>();

            for (String in : inputs)
            {
                String out = convert(in, inputType, outputType);
                ret.add(out);
            }
            return ret;
        }

        public static String convert(String in,
                                     Type inputType,
                                     Type outputType)
        {
            if (Type.s.equals(inputType) && Type.s.equals(outputType))
            {
                String asbi = BigIntUtilities.Human.createBigInteger(in).toString();
                String noop = BigIntUtilities.Human.createHumanString(new BigInteger(asbi));
                if (noop.equals(in))
                {
                    // that whole thing was a no-operation;  it was just a double-check
                    return in;
                }
                else
                {
                    throw new SecretShareException("Programmer error: in='" + in + "' asbi='" +
                                                    asbi + "' yet output string was '" + noop + "'");
                }
            }
            else if (Type.s.equals(inputType) && Type.bi.equals(outputType))
            {
                return BigIntUtilities.Human.createBigInteger(in).toString();
            }
            else if (Type.s.equals(inputType) && Type.bics.equals(outputType))
            {
                BigInteger inbi = BigIntUtilities.Human.createBigInteger(in);
                return BigIntUtilities.Checksum.createMd5CheckSumString(inbi);
            }

            else if (Type.bi.equals(inputType))
            {
                BigInteger inbi = new BigInteger(in);
                if (Type.s.equals(outputType))
                {
                    return BigIntUtilities.Human.createHumanString(inbi);
                }
                else if (Type.bi.equals(outputType))
                {
                    return inbi.toString();
                }
                else if (Type.bics.equals(outputType))
                {
                    return BigIntUtilities.Checksum.createMd5CheckSumString(inbi);
                }
                else
                {
                    error("input type bi, output type unknown: " + outputType);
                }
            }

            else if (Type.bics.equals(inputType))
            {
                BigInteger inbi = BigIntUtilities.Checksum.createBigInteger(in);
                if (Type.s.equals(outputType))
                {
                    return BigIntUtilities.Human.createHumanString(inbi);
                }
                else if (Type.bi.equals(outputType))
                {
                    return inbi.toString();
                }
                else if (Type.bics.equals(outputType))
                {
                    return BigIntUtilities.Checksum.createMd5CheckSumString(inbi);
                }
                else
                {
                    return error("input type bics, output type unknown: " + outputType);
                }
            }
            else
            {
                return error("input type unknown: " + inputType);
            }
            return error("Programmer Error - fell off if chain");
        }

        private static String error(String msg)
        {
            throw new SecretShareException(msg);
        }
        private void printHeaderInfo(PrintStream out)
        {
            out.print("Secret Share version " + Main.getVersionString());
            out.print(bigintcsInput.separator);
        }


    } // class BigIntCsOutput

}

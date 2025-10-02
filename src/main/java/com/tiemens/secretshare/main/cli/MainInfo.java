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
import java.util.LinkedHashMap;
import java.util.Map;

import com.tiemens.secretshare.engine.SecretShare;
import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.type.BigIntUtilities;

/**
 * Main command line for the "info" (aka "information") of secret share program/library.
 *
 * Prints the values of the built-in prime modulus.
 *
 * @author tiemens
 *
 */
public final class MainInfo
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
            InfoInput input = InfoInput.parse(args);
            InfoOutput output = input.output();
            output.print(out);
        }
        catch (SecretShareException e)
        {
            out.println(e.getMessage());
            usage(out);
            MainSplit.optionallyPrintStackTrace(args, e, out);
        }
    }

    public static void usage(PrintStream out)
    {
        out.println("Usage:");
        out.println(" info");
        // so far, no options available for 'info' command
        // out.println("  -prime8192    for modulus, use built-in 8192-bit prime");
        // out.println("  -prime4096    for modulus, use built-in 4096-bit prime");
        // out.println("  -prime384     for modulus, use built-in 384-bit prime [default]");
        // out.println("  -prime192     for modulus, use built-in 192-bit prime");
    }


    private MainInfo()
    {
        // no instances
    }


    public static class InfoInput
    {
        // ==================================================
        // instance data
        // ==================================================

        // required arguments:
        // none


        // ==================================================
        // constructors
        // ==================================================
        public static InfoInput parse(String[] args)
        {
            InfoInput ret = new InfoInput();

            for (int i = 0, n = args.length; i < n; i++)
            {
                if (args[i] == null)
                {
                    continue;
                }

                if (args[i].startsWith("-"))
                {
                    String m = "Argument '" + args[i] + "' not understood";
                    throw new SecretShareException(m);
                }
                else
                {
                    String m = "Extra argument '" + args[i] + "' not valid";
                    throw new SecretShareException(m);
                }
            }


            return ret;
        }


        // ==================================================
        // public methods
        // ==================================================
        public InfoOutput output()
        {
            InfoOutput ret = new InfoOutput(this);

            return ret;
        }


        // ==================================================
        // non public methods
        // ==================================================
    }

    public static class InfoOutput
    {
        private static final String SPACES = "                                              ";

        private final InfoInput infoInput;

        private final Map<String, BigInteger> description2BigInteger = new LinkedHashMap<String, BigInteger>();

        public InfoOutput(InfoInput inInfoInput)
        {
            infoInput = inInfoInput;

            description2BigInteger.put("Modulus 192 bits",
                                       SecretShare.getPrimeUsedFor192bitSecretPayload());
            description2BigInteger.put("Modulus 384 bits",
                                        SecretShare.getPrimeUsedFor384bitSecretPayload());
            description2BigInteger.put("Modulus 4096 bits",
                                        SecretShare.getPrimeUsedFor4096bigSecretPayload());
            description2BigInteger.put("Modulus 8192 bits",
                    SecretShare.getPrimeUsedFor8192bigSecretPayload());
        }

        public void print(PrintStream out)
        {
            if (infoInput == null)
            {
                throw new SecretShareException("null infoInput");
            }
            else
            {
                System.out.println(infoInput.getClass());
            }
            printHeaderInfo(out);
            printBuiltinPrimes(out);
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

        private void printBuiltinPrimes(PrintStream out)
        {
            for (String key : description2BigInteger.keySet())
            {
                markedValue(out, key, description2BigInteger.get(key), false);
                markedValue(out, key, description2BigInteger.get(key), true);
            }

        }


        private void printHeaderInfo(PrintStream out)
        {
            field(out, Main.getVersionLine(), "");
        }

        private void markedValue(PrintStream out,
                                 String fieldname,
                                 BigInteger number,
                                 boolean printAsBigIntCs)
        {
            String s;
            if (number != null)
            {
                if (printAsBigIntCs)
                {
                    s = BigIntUtilities.Checksum.createMd5CheckSumString(number);
                }
                else
                {
                    s = number.toString();
                }
                out.println(fieldname + " = " + s);
            }
            else
            {
                // no modulus supplied, do nothing
            }
        }

        private void field(PrintStream out,
                           String label,
                           String value)
        {
            final int fieldWidth = 30;
            if (value != null)
            {
                String sep;
                String pad;
                if ((label.length() > 0) &&
                    (! label.trim().equals("")))
                {
                    pad = label + SPACES;
                    pad = pad.substring(0, fieldWidth);
                    if (value.equals(""))
                    {
                        pad = label;
                        sep = "";
                    }
                    else
                    {
                        sep = ": ";
                    }
                }
                else
                {
                    pad = label;
                    sep = "";
                }

                out.println(pad + sep + value);
            }
        }
    } // class InfoOutput

}

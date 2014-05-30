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
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import com.tiemens.secretshare.engine.SecretShare;
import com.tiemens.secretshare.engine.SecretShare.ParanoidOutput;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import com.tiemens.secretshare.engine.SecretShare.SplitSecretOutput;
import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntUtilities;

/**
 * Main command line for the "split" (aka "create") of a secret.
 *
 * Takes a number of shares (n) and a threshold (k)
 *  and a secret (s) and creates the SecretShare.
 *
 * @author tiemens
 *
 */
public class MainSplit
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
            SplitInput input = SplitInput.parse(args);
            SplitOutput output = input.output();
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
        out.println(" split -k <k> -n <n> -sN|-sS secret " +               // required
                    "  [-prime4096|-prime384|-prime192|-primeN] [-d <desc>] [-paranoid <p>] "); // optional
        out.println("  -k <k>        the threshold");
        out.println("  -n <k>        the number of shares to generate");
        out.println("  -sN           the secret as a number, e.g. '124332' or 'bigintcs:01e5ac-787852'");
        out.println("  -sS           the secret as a string, e.g. 'My Secret'");
        out.println("  -d <desc>     description of the secret");
        out.println("  -prime4096    for modulus, use built-in 4096-bit prime");
        out.println("  -prime384     for modulus, use built-in 384-bit prime [default]");
        out.println("  -prime192     for modulus, use built-in 192-bit prime");
        out.println("  -primeAuto    for modulus, use 192, 384, 4096 or a random prime (that is bigger than secret)");
        out.println("  -primeN       same as -primeRandom");
        out.println("  -primeRandom  for modulus, use a random prime (that is bigger than secret)");
        out.println("  -m <modulus>  for modulus, use <modulus>, e.g. '11753999' or 'bigintcs:b35a0f-F89BEC'");
        out.println("  -primeNone    no modulus, do NOT use any modulus");
        out.println("  -paranoid <p> test combine combinations, maximum of <p> tests");
        out.println("  -printOne     put all shares on 1 sheet of paper");
        out.println("  -printIndiv   put 1 share per sheet, use 'n' sheets of paper");

    }



    public static BigInteger parseBigInteger(String argname,
                                             String[] args,
                                             int index)
    {
        checkIndex(argname, args, index);

        String value = args[index];
        BigInteger ret = null;
        if (BigIntUtilities.Checksum.couldCreateFromStringMd5CheckSum(value))
        {
            try
            {
                ret = BigIntUtilities.Checksum.createBigInteger(value);
            }
            catch (SecretShareException e)
            {
                String m = "Failed to parse 'bigintcs:' because: " + e.getMessage();
                throw new SecretShareException(m, e);
            }
        }
        else
        {
            try
            {
                ret = new BigInteger(value);
            }
            catch (NumberFormatException e)
            {
                String m = "Failed to parse integer because: " + e.getMessage();
                throw new SecretShareException(m, e);
            }
        }

        return ret;
    }

    public static Integer parseInt(String argname,
                                   String[] args,
                                   int index)
    {
        checkIndex(argname, args, index);
        String value = args[index];

        Integer ret = null;
        try
        {
            ret = Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            String m = "The argument of '" + value + "' " +
                               "is not a number.";
            throw new SecretShareException(m);
        }
        return ret;
    }


    public static void checkIndex(String argname,
                                  String[] args,
                                  int index)
    {
        if (index >= args.length)
        {
            throw new SecretShareException("The argument '-" + argname + "' requires an " +
                                           "additional argument");
        }
    }

    public static void optionallyPrintStackTrace(String[] args,
                                                 Exception e,
                                                 PrintStream out)
    {
        boolean print = false;
        for (String s : args)
        {
            if (s != null)
            {
                print = true;
            }
        }
        if (print)
        {
            e.printStackTrace(out);
        }
    }


    public static class SplitInput
    {
        // ==================================================
        // instance data
        // ==================================================

        // required arguments:
        private Integer k           = null;
        private Integer n           = null;
        private BigInteger secret   = null;

        // optional: if 'secret' was given as a human-string, this is non-null
        // else this is null
        private String secretArgument = null;

        // optional:  if null, then do not use modulus
        // default to 384-bit
        private BigInteger modulus = SecretShare.getPrimeUsedFor384bitSecretPayload();

        // optional:
        //    paranoid: null = do nothing, paranoid < 0 = do all, otherwise paranoid = # of tests
        private Integer paranoid;

        // optional description
        private String description = null;

        // optional: the random can be seeded
        private Random random;

        // if true, print on 1 sheet of paper; otherwise use 'n' sheets and repeat the header
        private boolean printAllSharesAtOnce = true;

        // ==================================================
        // constructors
        // ==================================================
        public static SplitInput parse(String[] args)
        {
            SplitInput ret = new SplitInput();

            boolean calculateModulus = false;
            boolean calculateModulusAuto = true;
            for (int i = 0, n = args.length; i < n; i++)
            {
                if (args[i] == null)
                {
                    continue;
                }

                if ("-k".equals(args[i]))
                {
                    i++;
                    ret.k = parseInt("k", args, i);
                }
                else if ("-n".equals(args[i]))
                {
                    i++;
                    ret.n = parseInt("n", args, i);
                }
                else if ("-d".equals(args[i]))
                {
                    i++;
                    checkIndex("d", args, i);
                    ret.description = args[i];
                }
                else if ("-sN".equals(args[i]))
                {
                    i++;
                    ret.secretArgument = null;
                    ret.secret = parseBigInteger("sN", args, i);
                }
                else if ("-sS".equals(args[i]))
                {
                    i++;
                    ret.secretArgument = args[i];
                    ret.secret = BigIntUtilities.Human.createBigInteger(args[i]);
                }
                else if ("-r".equals(args[i]))
                {
                    i++;
                    int seed =  parseInt("r", args, i);
                    ret.random = new Random(seed);
                }
                else if ("-prime4096".equals(args[i]))
                {
                    ret.modulus = SecretShare.getPrimeUsedFor4096bigSecretPayload();
                }
                else if ("-prime384".equals(args[i]))
                {
                    ret.modulus = SecretShare.getPrimeUsedFor384bitSecretPayload();
                }
                else if ("-prime192".equals(args[i]))
                {
                    ret.modulus = SecretShare.getPrimeUsedFor192bitSecretPayload();
                }
                else if ("-primeAuto".equals(args[i]))
                {
                    calculateModulus = true;
                    calculateModulusAuto = true;
                }
                else if (("-primeRandom".equals(args[i])) ||
                         ("-primeN".equals(args[i])))    // backward-compatible
                {
                    calculateModulus = true;
                    calculateModulusAuto = false;
                }
                else if ("-primeNone".equals(args[i]))
                {
                    calculateModulus = false;
                    ret.modulus = null;
                }
                else if ("-m".equals(args[i]))
                {
                    calculateModulus = false;
                    i++;
                    final String thearg = args[i];
                    if (BigIntUtilities.Checksum.couldCreateFromStringMd5CheckSum(thearg))
                    {
                        ret.modulus = BigIntUtilities.Checksum.createBiscs(thearg).asBigInteger();
                    }
                    else
                    {
                        ret.modulus = new BigInteger(thearg);
                    }
                }

                else if ("-paranoid".equals(args[i]))
                {
                    i++;
                    if ("all".equals(args[i]))
                    {
                        ret.paranoid = -1;
                    }
                    else
                    {
                        ret.paranoid = parseInt("paranoid", args, i);
                    }
                }
                else if ("-printOne".equals(args[i]))
                {
                    ret.printAllSharesAtOnce = true;
                }
                else if (args[i].startsWith("-printIndiv"))  // -printIndividual
                {
                    ret.printAllSharesAtOnce = false;
                }
                else if (args[i].startsWith("-"))
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

            checkRequired("-k", ret.k);
            checkRequired("-n", ret.n);
            checkRequired("-sN or -sS", ret.secret);

            if (calculateModulus)
            {
                if (calculateModulusAuto)
                {
                    ret.modulus = SecretShare.createAppropriateModulusForSecret(ret.secret);
                }
                else
                {
                    ret.modulus = SecretShare.createRandomModulusForSecret(ret.secret);
                }
            }

            if (ret.modulus != null)
            {
                if (! SecretShare.isTheModulusAppropriateForSecret(ret.modulus, ret.secret))
                {
                    final String originalString;
                    if (ret.secretArgument != null)
                    {
                        originalString = "[" + ret.secretArgument + "]";
                    }
                    else
                    {
                        originalString = "";
                    }

                    final String sInfo;
                    String sAsString = "" + ret.secret;
                    if (sAsString.length() < 25)
                    {
                        sInfo = sAsString;
                    }
                    else
                    {
                        sInfo = "length is " + sAsString.length() + " digits";
                    }
                    String m = "The secret " + originalString +  " (" + sInfo + ") is too big.  " +
                            "Please adjust the prime modulus or use -primeNone";

                    throw new SecretShareException(m);

                }
            }

            if (ret.random == null)
            {
                ret.random = new SecureRandom();
            }
            return ret;
        }

        private static void checkRequired(String argname,
                                          Object obj)
        {
            if (obj == null)
            {
                throw new SecretShareException("Argument '" + argname + "' is required.");
            }
        }

        // ==================================================
        // public methods
        // ==================================================
        public SplitOutput output()
        {
            SplitOutput ret = new SplitOutput();
            ret.setPrintAllSharesAtOnce(printAllSharesAtOnce);
            ret.splitInput = this;

            SecretShare.PublicInfo publicInfo =
                new SecretShare.PublicInfo(this.n,
                                           this.k,
                                           this.modulus,
                                           this.description);

            SecretShare secretShare = new SecretShare(publicInfo);
            Random random = this.random;

            SecretShare.SplitSecretOutput generate = secretShare.split(secret, random);

            ret.splitSecretOutput = generate;

            if (paranoid != null)
            {
                Integer parg = paranoid;
                if (parg < 0)
                {
                    parg = null;
                }

                ret.paranoidOutput =
                        secretShare.combineParanoid(generate.getShareInfos(),
                                                    parg);
            }
            else
            {
                ret.paranoidOutput = null;
            }

            return ret;
        }


        // ==================================================
        // non public methods
        // ==================================================
    }

    public static class SplitOutput
    {
        private static String SPACES = "                                              ";
        private boolean printAllSharesAtOnce = true;

        public SplitInput splitInput;
        private SplitSecretOutput splitSecretOutput;
        private ParanoidOutput paranoidOutput = null; // can be null

        public SplitOutput()
        {
            this(true);
        }
        public SplitOutput(boolean inPrintAllSharesAtOnce)
        {
            printAllSharesAtOnce = inPrintAllSharesAtOnce;
        }

        public void setPrintAllSharesAtOnce(boolean val)
        {
            printAllSharesAtOnce = val;
        }

        public void print(PrintStream out)
        {
            if (printAllSharesAtOnce)
            {
                printParanoidCompleteOutput(out);
                printHeaderInfo(out);
                printSharesAllAtOnce(out);
            }
            else
            {
                printSharesOnePerPage(out);
            }
        }
        private boolean hasParanoidOutput()
        {
            return (paranoidOutput != null);
        }
        private void printParanoidCompleteOutput(PrintStream out)
        {
            if (hasParanoidOutput())
            {
                out.println(paranoidOutput.getParanoidCompleteOutput());
            }
        }
        private void printParanoidHeaderOutput(PrintStream out)
        {
            if (hasParanoidOutput())
            {

                out.println(paranoidOutput.getParanoidHeaderOutput());
            }
        }

        private void printSharesOnePerPage(PrintStream out)
        {
            final List<SecretShare.ShareInfo> shares = splitSecretOutput.getShareInfos();
            boolean first = true;
            for (SecretShare.ShareInfo share : shares)
            {
                if (! first)
                {
                    printSeparatePage(out);
                }
                first = false;

                printHeaderInfo(out);

                if (hasParanoidOutput())
                {
                    out.println("(Re-)Combine testing performed and passed.");
                    printParanoidHeaderOutput(out);
                }

                printShare(out, share, false);
                printShare(out, share, true);

            }

        }

        private void printSeparatePage(PrintStream out)
        {
            out.print("\u000C");
        }

        private void printHeaderInfo(PrintStream out)
        {
            final SecretShare.PublicInfo publicInfo = splitSecretOutput.getPublicInfo();

            field(out, "Secret Share version " + Main.getVersionString(), "");
            field(out, "Date", publicInfo.getDate());
            field(out, "UUID", publicInfo.getUuid());
            field(out, "Description", publicInfo.getDescription());

            markedValue(out, "n", publicInfo.getN());
            markedValue(out, "k", publicInfo.getK());
            markedValue(out, "modulus", publicInfo.getPrimeModulus(), false);
            markedValue(out, "modulus", publicInfo.getPrimeModulus(), true);
        }

        private void printSharesAllAtOnce(PrintStream out)
        {
            List<SecretShare.ShareInfo> shares = splitSecretOutput.getShareInfos();
            out.println("");
            for (SecretShare.ShareInfo share : shares)
            {
                printShare(out, share, false);
            }
            for (SecretShare.ShareInfo share : shares)
            {
                printShare(out, share, true);
            }
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
        private void markedValue(PrintStream out,
                                 String fieldname,
                                 int n)
        {
            out.println(fieldname + " = " + n);
        }

        // ==================================================
        // instance data
        // ==================================================

        private void field(PrintStream out,
                           String label,
                           BigInteger number)
        {
            if (number != null)
            {
                String spaces;
                if (label.trim().equals(""))
                {
                    spaces = SPACES.substring(0, label.length());
                }
                else
                {
                    spaces = "." + SPACES.substring(0, label.length() - 1);
                }

                field(out, label, number.toString());
                field(out, spaces, BigIntUtilities.Checksum.createMd5CheckSumString(number));
            }
            else
            {
                // no output
            }
        }

        private void field(PrintStream out,
                           String label,
                           String value)
        {
            if (value != null)
            {
                String sep;
                String pad;
                if ((label.length() > 0) &&
                    (! label.trim().equals("")))
                {
                    pad = label + SPACES;
                    pad = pad.substring(0, 30);
                    if (value.equals(""))
                    {
                        sep = "  ";
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

        private void printShare(PrintStream out,
                                ShareInfo share,
                                boolean printAsBigIntCs)
        {
            markedValue(out, "Share (x:" + share.getIndex() + ")", share.getShare(), printAsBigIntCs);
        }


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
}

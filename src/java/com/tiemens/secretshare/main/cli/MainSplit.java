package com.tiemens.secretshare.main.cli;

import java.io.PrintStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import com.tiemens.secretshare.engine.SecretShare;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import com.tiemens.secretshare.engine.SecretShare.SplitSecretOutput;
import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.math.BigIntUtilities;

/**
 * Main command line for the "split" (aka create") of a secret.
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
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            SplitInput input = SplitInput.parse(args);
            SplitOutput output = input.output();
            output.print(System.out);
        }
        catch (SecretShareException e)
        {
            System.out.println(e.getMessage());
            usage();
            optionallyPrintStackTrace(args, e);
        }
    }

    public static void usage()
    {
        System.out.println("Usage:");
        System.out.println(" split -k <k> -n <n> -sN|-sS secret " +               // required
                             "  [-prime384|-prime192|-primeN] [-d <desc>] [-paranoid <p>] "); // optional
        System.out.println("  -k <k>        the threshold");
        System.out.println("  -n <k>        the number of shares to generate");
        System.out.println("  -sN           the secret as a number, e.g. '124332' or 'bigintcs:123456-DC0AE1'");
        System.out.println("  -sS           the secret as a string, e.g. 'My Secret'");
        System.out.println("  -d <desc>     description of the secret");
        System.out.println("  -prime384     for modulus, use built-in 384-bit prime [default]");
        System.out.println("  -prime192     for modulus, use built-in 192-bit prime");
        System.out.println("  -primeN       for modulus, use a random prime (that is bigger than secret)");
        System.out.println("  -primeNone    modulus, do NOT use any modulus");
        System.out.println("  -m <modulus>  for modulus, use <modulus>");
        System.out.println("  -paranoid <p> test combine combinations, maximum of <p> tests");

    }

    
    
    public static BigInteger parseBigInteger(String argname,
                                             String[] args,
                                             int index)
    {
        checkIndex(argname, args, index);
        
        String value = args[index];
        BigInteger ret = null;
        if (BigIntUtilities.couldCreateFromStringMd5CheckSum(value))
        {
            try
            {
                ret = BigIntUtilities.createFromStringMd5CheckSum(value);
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
    
    
    private static void checkIndex(String argname,
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
                                                 Exception e)
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
            e.printStackTrace();
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
        
        // ==================================================
        // constructors
        // ==================================================
        public static SplitInput parse(String[] args)
        {
            SplitInput ret = new SplitInput();

            boolean calculateModulus = false;
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
                    ret.description = args[i];
                }
                else if ("-sN".equals(args[i]))
                {
                    i++;
                    ret.secret = parseBigInteger("sN", args, i);
                }
                else if ("-sS".equals(args[i]))
                {
                    i++;
                    ret.secretArgument = args[i];
                    ret.secret = BigIntUtilities.createFromStringsBytesAsData(args[i]);
                }
                else if ("-r".equals(args[i]))
                {
                    i++;
                    int seed =  parseInt("r", args, i);
                    ret.random = new Random(seed);
                }
                else if ("-prime384".equals(args[i]))
                {
                    ret.modulus = SecretShare.getPrimeUsedFor384bitSecretPayload();
                }
                else if ("-prime192".equals(args[i]))
                {
                    ret.modulus = SecretShare.getPrimeUsedFor192bitSecretPayload();
                }
                else if ("-primeN".equals(args[i]))
                {
                    calculateModulus = true;
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
                    ret.modulus = BigIntUtilities.createFromStringsBytesAsData(args[i]);
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
                ret.modulus = BigIntUtilities.createPrimeBigger(ret.secret);
            }
            
            if (ret.modulus != null)
            {
                if (ret.secret.compareTo(ret.modulus) >= 0)
                {
                    String m = "The secret is too big.  Please adjust the prime modulus with either " +
                        "-primeN or -primeNone";
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
                
                secretShare.combineParanoid(generate.getShareInfos(),
                                            parg,
                                            this.getParanoidOutput());
            }
            return ret;
        }

        private PrintStream getParanoidOutput()
        {
            return System.out;
        }
        
        // ==================================================
        // non public methods
        // ==================================================
    }
    
    public static class SplitOutput
    {
        private static String SPACES = "                                              ";
        
        public SplitInput splitInput;
        private SplitSecretOutput splitSecretOutput;

        public void print(PrintStream out)
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
            if (printAsBigIntCs)
            {
                s = BigIntUtilities.createStringMd5CheckSumFromBigInteger(number);
            }
            else
            {
                s = number.toString();
            }
            out.println(fieldname + " = " + s);
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
                field(out, spaces, BigIntUtilities.createStringMd5CheckSumFromBigInteger(number));
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
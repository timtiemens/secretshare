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
package com.tiemens.secretshare.main.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.tiemens.secretshare.engine.SecretShare;
import com.tiemens.secretshare.engine.SecretShare.PublicInfo;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
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
public class MainCombine
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            CombineInput input = CombineInput.parse(args);
            CombineOutput output = input.output();
            output.print(System.out);
        }
        catch (SecretShareException e)
        {
            System.out.println(e.getMessage());
            usage();
            MainSplit.optionallyPrintStackTrace(args, e);
        }
    }

    public static void usage()
    {
        System.out.println("Usage:");
        System.out.println(" combine -k <k>  -s<n> <secret-N> ..." +               // required
                             "  [-prime384|-prime192|-primeN <m>|-primeNone] -stdin"); // optional
        System.out.println("  -k <k>        the threshold");
        System.out.println("  -s<n> <s>     secret#n as a number e.g. '124332' or 'bigintcs:123456-DC0AE1'");
        System.out.println("     ...           repeat this argument <k> times");
        System.out.println("  -prime384     for modulus, use built-in 384-bit prime [default]");
        System.out.println("  -prime192     for modulus, use built-in 192-bit prime");
        System.out.println("  -primeN <m>   for modulus use m, e.g. '59561' or 'bigintcs:12345-DC0AE1'");
        System.out.println("  -primeNone    modulus, do NOT use any modulus");
        System.out.println("  -stdin        read values from standard input, as written by 'split'");
    }

    
    
    private static BigInteger parseBigInteger(String argname,
                                              String[] args,
                                              int index)
    {
        return MainSplit.parseBigInteger(argname, args, index);
    }

    private static Integer parseInt(String argname,
                                    String[] args,
                                    int index)
    {
        return MainSplit.parseInt(argname, args, index);
    }
    
    

    public static class CombineInput
    {
        // ==================================================
        // instance data
        // ==================================================
        
        // required arguments:
        private Integer k           = null;
        
        private List<SecretShare.ShareInfo> shares = new ArrayList<SecretShare.ShareInfo>();

        // optional:  if null, then do not use modulus 
        // default to 384-bit
        private BigInteger modulus = SecretShare.getPrimeUsedFor384bitSecretPayload();

        // optional: for combine, we don't need n, but you can provide it
        private Integer n           = null;

        private PublicInfo publicInfo;
        // ==================================================
        // constructors
        // ==================================================
        public static CombineInput parse(String[] args)
        {
            CombineInput ret = new CombineInput();

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
                    if (ret.n == null)
                    {
                        ret.n = ret.k;
                    }
                }
                else if ("-n".equals(args[i]))
                {
                    i++;
                    ret.n = parseInt("n", args, i);
                }
                else if ("-stdin".equals(args[i]))
                {
                    ret.processStdin();
                }
                else if ("-m".equals(args[i]))
                {
                    i++;
                    ret.modulus = parseBigInteger("m", args, i);
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
                    i++;
                    ret.modulus = parseBigInteger("primeN", args, i);
                }
                else if ("-primeNone".equals(args[i]))
                {
                    ret.modulus = null;
                }
                else if (args[i].startsWith("-s"))
                {
                    String number = args[i].substring(2);
                    i++;
                    MainSplit.checkIndex("s", args, i);
                    String line = "Share (x:" + number + ") = " + args[i]; 
                    SecretShare.ShareInfo share = ret.parseEqualShare("-s", line);
                    // TODO; better checking for duplicates
                    ret.addIfNotDuplicate(share);
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
            if (ret.shares.size() < ret.k)
            {
                throw new SecretShareException("k set to " + ret.k + " but only " +
                                               ret.shares.size() + " shares provided");
            }
            
            return ret;
        }

        private void processStdin()
        {
            try
            {
                processStdinThrow();
            }
            catch (IOException e)
            {
                throw new SecretShareException("IOException reading stdin", e);
            }
        }
        
        // examples of the kinds of lines we look for:
        
        //  n = 6
        //  k = 3
        //  modulus = 830856716641269307206384693584652377753448639527
        //  modulus = bigintcs:000002-dba253-6f54b0-ec6c27-3198DB
        //  Share (x:1) = 481883688219928417596627230876804843822861100800
        //  Share (x:2) = 481883688232565050752267350226995441999530323860
        //  Share (x:1) = bigintcs:005468-697323-cc48a7-8f1f87-996040-4d07d2-3da700-9C4722
        //  Share (x:2) = bigintcs:005468-69732d-4e02c5-7b11d2-9d4426-e26c88-8a6f94-9809A9
        private void processStdinThrow() 
            throws IOException
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.startsWith("n ="))
                {
                    this.n = parseEqualInt("n", line);
                }
                else if (line.startsWith("k ="))
                {
                    this.k = parseEqualInt("k", line);
                }
                else if (line.startsWith("modulus ="))
                {
                    this.modulus = parseEqualBigInt("modulus", line);

                }
                else if (line.startsWith("Share ("))
                {
                    SecretShare.ShareInfo share = parseEqualShare("share", line);
                    addIfNotDuplicate(share);
                }
            }
            
        }

        private void addIfNotDuplicate(ShareInfo add)
        {
            boolean shouldadd = true;
            for (ShareInfo share : this.shares)
            {
                if (share.getX() == add.getX())
                {
                    // dupe
                    if (! share.getShare().equals(add.getShare()))
                    {
                        throw new SecretShareException("share x:" + share.getX() + 
                                                       " was entered with two different values " +
                                                       "(" + share.getShare() + ") and (" +
                                                       add.getShare() + ")");
                    }
                    else
                    {
                        shouldadd = false;
                    }
                }
                else if (share.getShare().equals(add.getShare())) 
                {
                    throw new SecretShareException("duplicate share values at x:" +
                                                   share.getX() + " and x:" + 
                                                   add.getX());
                }
            }
            if (shouldadd)
            {
                this.shares.add(add);
            }
        }

        private ShareInfo parseEqualShare(String fieldname,
                                          String line)
        {
            if (this.publicInfo == null)
            {
                this.publicInfo = new SecretShare.PublicInfo(n, k, this.modulus, "");
            }

            BigInteger s = parseEqualBigInt(fieldname, line);
            int x = parseXcolon(line);
            return new ShareInfo(x, s, this.publicInfo);
        }

        //  Share (x:2) = bigintcs:005468-69732d-4e02c5-7b11d2-9d4426-e26c88-8a6f94-9809A9
        private int parseXcolon(String line)
        {
            String i = after(line, ":");
            int end = i.indexOf(")");
            i = i.substring(0, end);

            return Integer.valueOf(i);
        }

        private BigInteger parseEqualBigInt(String fieldname,
                                            String line)
        {
            String s = after(line, "=");
            if (BigIntUtilities.couldCreateFromStringMd5CheckSum(s))
            {
                return BigIntUtilities.createFromStringMd5CheckSum(s);
            }
            else if (BigIntUtilities.couldCreateFromHexString(s))
            {
                return BigIntUtilities.createFromHexString(s);
            }
            else
            {
                return new BigInteger(s);
            }
        }

        private String after(String line,
                             String lookfor)
        {
            return line.substring(line.indexOf(lookfor) + 1).trim();
        }

        private Integer parseEqualInt(String fieldname,
                                      String line)
        {
            String s = after(line, "=");
            return Integer.valueOf(s);
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
        public CombineOutput output()
        {
            CombineOutput ret = new CombineOutput();
            ret.combineInput = this;
            
            SecretShare.PublicInfo publicInfo = 
                new SecretShare.PublicInfo(this.n, 
                                           this.k, 
                                           this.modulus,
                                           "recombine combine command line");
            
            SecretShare secretShare = new SecretShare(publicInfo);
            
            SecretShare.CombineOutput combine= secretShare.combine(shares);

            ret.secret = combine.getSecret();
            
            return ret;
        }
        
        // ==================================================
        // non public methods
        // ==================================================
    }
    
    public static class CombineOutput
    {
        
        private BigInteger secret;
        
        @SuppressWarnings("unused")
        private SecretShare.CombineOutput combineOutput;
        @SuppressWarnings("unused")
        private CombineInput combineInput;

        public void print(PrintStream out)
        {
            //final SecretShare.PublicInfo publicInfo = combineOutput.getPublicInfo();
            
            out.println("Secret Share version " + Main.getVersionString());
            //field(out, "Date", publicInfo.getDate());
            //field(out, "UUID", publicInfo.getUuid());
            //field(out, "Description", publicInfo.getDescription());
            
            out.println("secret.number = '" + secret + "'");
            String s = BigIntUtilities.createStringFromBigInteger(secret);
            out.println("secret.string = '" + s + "'");

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
    }
}

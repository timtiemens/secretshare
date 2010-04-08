package com.tiemens.secretshare.math;

import java.math.BigInteger;

import com.tiemens.secretshare.exceptions.SecretShareException;
import com.tiemens.secretshare.md5sum.Md5Checksummer;
import com.tiemens.secretshare.md5sum.Md5ChecksummerFactory;
import com.tiemens.secretshare.md5sum.Md5ChecksummerImpl;


/**
 * Data structure to support a "URI-like" string that allows BigInteger
 * values to be encoded as a hex string with a checksum. Syntax:
 * bigintcs:HHHHHH-HHHHHH-CCCCCC Example:
 * bigintcs:bd2c52-b16d74-d51456-d0f89a-30c932-b2f6c1-3a9ce3-7b4387-0F2CA0
 * 
 * Note: negative BigIntegers are supported.  This adds a "-" at the front:
 * e.g. -100 is the string 'bigintcs:-000064-BBC6EC'
 * The checksum takes the "-" into account, therefore
 * 'bigintcs:-000064-BBC6EC' = -100
 * 'bigintcs:000064-BBC6EC'  = error
 * 
 * The "HHHHHH-" section is repeated as many times as needed. 
 * The first "HHHHHH-" section is 0-padded as needed. 
 * The first section is either "HHHHHH" or "-HHHHHH" [dash means negative BigInteger].
 * 
 * The case of a-f versus A-F does not matter. 
 * Convention is that the HHHHHH- section is lower case, the CCCCCC-
 * is upper case.
 * 
 */
public final class BigIntStringChecksum
{


    // ==================================================
    // class static data
    // ==================================================
    
    /**
     * The Prefix string that identifies the 6hex-6hex-md5sum6hex pattern
     *   we've invented here.
     */
    public static final String PREFIX_BIGINT_DASH_CHECKSUM = "bigintcs:";

    /**
     * Constant for how many characters/digits are in each "6hex" section.
     */
    private static final int   DIGITS_PER_GROUP            = 6;
    
    
    private static final int HEX_RADIX                     = 16;

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================
    /**
     * Contains a hex-encoded string of the big integer.
     * Does not contain dashes ("-")
     */
    private final String       asHex;
    
    /**
     * Contains a hex-encoded string of the md5sum of "asHex".
     * Our implementation is limited to 6-characters.
     */
    private final String       md5checksum;

    // ==================================================
    // factories
    // ==================================================

    /**
     * Take this.toString() as input, and create the instance.
     * 
     * @param bics string in "bigintcs:HHHHHH-HHHHHH-CCCCCC" format
     * @return big integer string checksum object
     * @throws SecretShareException on error
     */
    public static BigIntStringChecksum fromString(String bics)
    {
        boolean returnIsNegative = false;
        BigIntStringChecksum ret = null;
        if (bics == null)
        {
            createThrow("Input cannot be null", bics);
        }
        if (bics.startsWith(PREFIX_BIGINT_DASH_CHECKSUM))
        {
            String noprefix = bics.substring(PREFIX_BIGINT_DASH_CHECKSUM.length());
            String noprefixnosign = noprefix;
            if (noprefixnosign.startsWith("-"))
            {
                returnIsNegative = true;
                noprefixnosign = noprefixnosign.substring(1);
            }
            String[] split = noprefixnosign.split("-");
            if (split.length <= 1)
            {
                createThrow("Missing checksum section", bics);
            }
            else
            {
                String asHex = "";
                if (returnIsNegative)
                {
                    asHex = "-";
                }
                for (int i = 0, n = split.length - 1; i < n; i++)
                {
                    asHex += split[i];
                }
                String computedMd5sum = computeMd5ChecksumLimit6(asHex);
                String givenMd5sum = split[split.length - 1];
                if (computedMd5sum.equalsIgnoreCase(givenMd5sum))
                {
                    ret = new BigIntStringChecksum(asHex, computedMd5sum);
                }
                else 
                {
                    createThrow("Mismatch checksum given='" + givenMd5sum +
                                "' computed='" + computedMd5sum + "'", bics);
                }
            }
        }
        else
        {
            createThrow("Input must start with '" +
                        PREFIX_BIGINT_DASH_CHECKSUM + "'",
                        bics);
        }
        // String hex6perdash = convertHexToDash(asHex);
        // return PREFIX_BIGINT_DASH_CHECKSUM +
        // hex6perdash +
        // "-" +
        // md5checksum;
        
        return ret;
    }

    public static BigIntStringChecksum create(final BigInteger in)
    {
        if (in == null)
        {
            throw new SecretShareException("Input biginteger cannot be null");
        }

        final String inHex = in.toString(HEX_RADIX);
        final String inAsHex = pad(inHex);


        String md5checksum = computeMd5ChecksumLimit6(inAsHex);

        return new BigIntStringChecksum(inAsHex, md5checksum);


    }
    

    public String toString()
    {
        String hex6perdash = insertDashesIntoHex(asHex);
        return PREFIX_BIGINT_DASH_CHECKSUM +
               hex6perdash +
               "-" +
               md5checksum;
    }

    public BigInteger asBigInteger()
    {
        try
        {
            return new BigInteger(asHex, HEX_RADIX);
        }
        catch (NumberFormatException e)
        {
            throw new SecretShareException("Invalid input='" + asHex + "'", e);
        }
    }

    // ==================================================
    // constructors
    // ==================================================
    public BigIntStringChecksum(final String inAsHex,
                                final String inMd5checksum)
    {
        asHex = inAsHex;
        md5checksum = inMd5checksum;
    }

    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================
    
    /* private */static String bytesToHexString(byte... in)
    {
        String ret = "";
        for (byte b : in)
        {
            ret += byteToHexString(b);
        }
        return ret;
    }

    private static String lookup[] = { "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "A", "B", "C", "D", "E", "F" };

    private static String byteToHexString(byte b)
    {
        String ret = "";
        byte ch = 0x00;
        ch = (byte) (b & 0xF0); // strip off high
        ch = (byte) (ch >>> 4); // shift
        ch = (byte) (ch & 0X0F); // the >>> turned on high bits, get rid of them
        ret += lookup[(int) ch];

        ch = (byte) (b & 0X0F);
        ret += lookup[(int) ch];

        return ret;
    }

    private static String insertDashesIntoHex(final String inAsHex)
    {
        String ret = "";
        int LEN_PER_GROUP = 6;
        String input = inAsHex;
        boolean returnIsNegative = false;
        if (input.startsWith("-"))
        {
            returnIsNegative = true;
            input = input.substring(1);
        }
        while ((input.length() % LEN_PER_GROUP) != 0)
        {
            input = "0" + input;
        }
        String sep = "";
        for (int i = 0, n = input.length() / LEN_PER_GROUP; i < n; i++)
        {
            ret += sep;
            sep = "-";
            ret += input.substring((i + 0) * LEN_PER_GROUP,
                                   (i + 1) * LEN_PER_GROUP);
        }
        if (returnIsNegative)
        {
            ret = "-" + ret;
        }
        return ret;
    }
    
    
    private static byte[] computeMd5ChecksumFull(String inAsHex2)
    {
       Md5Checksummer md5summer = null;
       
       md5summer = new Md5ChecksummerImpl();
       
       if (false)
       {
       // Test over-ride on class name:
           md5summer = Md5ChecksummerFactory
               .createFromClassName("com.tiemens.secretshare.md5sum.Md5ChecksummerImpl");
       }
        
        byte[] bytes = md5summer.createMd5Checksum(inAsHex2.toLowerCase().getBytes());

        return bytes;
    }
    private static String computeMd5ChecksumLimit6(String inAsHex2)
    {
        byte[] bytes = computeMd5ChecksumFull(inAsHex2);
        
        String md5checksum = bytesToHexString(bytes[2],
                                              bytes[1],
                                              bytes[0]);
        return md5checksum;
    }


    private static void createThrow(String string,
                                    String bics)
    {
        throw new SecretShareException(string + "(input=" + bics + ")");
    }

 
    private static String pad(final String inHex)
    {
        int LEN_PER_GROUP = DIGITS_PER_GROUP;
        String useHex = inHex;
        boolean returnIsNegative = false;
        if (useHex.startsWith("-"))
        {
            useHex = useHex.substring(1);
            returnIsNegative = true;
        }
        String ret = useHex;
        while ((ret.length() % LEN_PER_GROUP) != 0)
        {
            ret = "0" + ret;
        }
        if (returnIsNegative)
        {
            ret = "-" + ret;
        }
        return ret;
    }


}
package com.tiemens.secretshare.math;

public final class HexByteUtilities
{


    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================


    public static byte[] hexToBytes(char[] hex)
    {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++)
        {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127)
            {
                value -= 256;
            }
            raw[i] = (byte) value;
        }
        return raw;
    }

    public static byte[] hexToBytes(String hex)
    {
      return hexToBytes(hex.toCharArray());
    }

    public static String printAsHex(byte[] bytes)
    {
        String s2 = BigIntStringChecksum.bytesToHexString(bytes);
        return s2;
    }


    // ==================================================
    // instance data
    // ==================================================

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    private HexByteUtilities()
    {
        // no instances
    }

    // ==================================================
    // public methods
    // ==================================================

    // ==================================================
    // non public methods
    // ==================================================
}
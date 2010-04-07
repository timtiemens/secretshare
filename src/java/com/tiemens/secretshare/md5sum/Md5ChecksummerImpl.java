package com.tiemens.secretshare.md5sum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.tiemens.secretshare.exceptions.SecretShareException;

public class Md5ChecksummerImpl
        implements
            Md5Checksummer
{


    // ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================
    private final MessageDigest digest;
    
    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================
    
    /**
     * @throws SecretShareException if something goes wrong on construction 
     */
    public Md5ChecksummerImpl()
    {
        try
        {
            digest = java.security.MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new SecretShareException("failed to create md5 digest", e);
        }        
    }
    
    // ==================================================
    // public methods
    // ==================================================
    @Override
    public synchronized byte[] createMd5Checksum(final byte[] in)
    {
        digest.reset();

        digest.update(in);
        
        byte[] bytes = digest.digest();

        return bytes;
    }

    // ==================================================
    // non public methods
    // ==================================================
}
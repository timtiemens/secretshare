package com.tiemens.secretshare.md5sum;

public interface Md5Checksummer
{
    /**
     * @param in the byte array to compute a checksum
     * @return the complete md5 checksum
     */
    public byte[] createMd5Checksum(final byte[] in);
}
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
package com.tiemens.secretshare.md5sum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.tiemens.secretshare.exceptions.SecretShareException;

public class Md5ChecksummerImpl
    implements Md5Checksummer
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

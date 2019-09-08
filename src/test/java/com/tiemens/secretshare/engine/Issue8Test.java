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
package com.tiemens.secretshare.engine;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Assert;
import org.junit.Test;

import com.tiemens.secretshare.math.type.BigIntUtilities;

public class Issue8Test
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

    // ==================================================
    // factories
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    // @Before


    // ==================================================
    // public methods
    // ==================================================

    private static String getSecret(int maxBytes)
    {
        StringBuilder secret = new StringBuilder();
        for (int i = 0; i < maxBytes; i++)
        {
            secret.append((char) (65 + (i % 26)));
        }
        return secret.toString();
    }


    @Test
    public void testSecretPartialLeak()
    {
        subtest(SecretShare.getPrimeUsedFor192bitSecretPayload(), 192 / 8);
        subtest(SecretShare.getPrimeUsedFor4096bigSecretPayload(), 4096 / 8);
    }

    private void subtest(final BigInteger modulus, final int maxBytes)
    {
        String secretString = getSecret(maxBytes);
        //System.out.println("Secret String  : " + secretString);
        int k = 2;
        int n = 3;
        BigInteger secret = BigIntUtilities.Human.createBigInteger(secretString);

        SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n, k, modulus, null);

        SecretShare secretShare = new SecretShare(publicInfo);
        SecretShare.SplitSecretOutput generate = secretShare.split(secret, new SecureRandom());

        for (SecretShare.ShareInfo shareInfo : generate.getShareInfos())
        {
            BigInteger share = shareInfo.getShare();
            String s = BigIntUtilities.Human.createHumanString(share);

            //System.out.println("Share as string: " + s);

            // Arbitrarily set the maximum acceptable "leak" at 4 bytes:
            //   (The current implementation usually "leaks" at most 1 bit - but coefficients are randomly picked)
            Assert.assertFalse("Too Many Leaked Bytes", s.startsWith("ABCD"));
        }


    }

    // ==================================================
    // non public methods
    // ==================================================


}

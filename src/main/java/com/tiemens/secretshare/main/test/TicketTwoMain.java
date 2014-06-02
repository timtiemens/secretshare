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
package com.tiemens.secretshare.main.test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tiemens.secretshare.engine.SecretShare;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;

public class TicketTwoMain
{
    public static void main(String... args)
            throws UnsupportedEncodingException
    {

        // We got a secret to split into n pieces,
        // but the secret is re-constructable from any k of them
        final String secret;

        boolean useSimpleSecret = false;

        if (useSimpleSecret)
        {
            // simple secret:
            secret = "Hello Secret!";
        }
        else
        {
            // This secret takes a long time to generate "probable prime"...
            secret = "A2345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "1234567890123456789012345678901234567890123456789Z";
        }

        System.out.println("Secret as number: " + stringToBigInteger(secret));
        final int n = 6, k = 6;
        String[] pieces = splitSecretIntoPieces(secret, n, k);
        System.out.println(n + " pieces: " + Arrays.toString(pieces));
        // Shuffle the 6 pieces
        List<String> list = new ArrayList<String>(Arrays.asList(pieces));
        Collections.shuffle(list);
        // Reconstruct the secret using any 3 pieces
        String[] kPieces = list.toArray(new String[0]);
        System.out.println("Any " + k + " pieces: " + Arrays.toString(kPieces));
        String reconstructed = mergePiecesIntoSecret(kPieces);
        System.out.println("Reconstructed secret=" + reconstructed);

        System.out.println("Finish");
    }

    static BigInteger stringToBigInteger(String in)
            throws UnsupportedEncodingException
    {
        final BigInteger bigint = new BigInteger(in.getBytes("UTF-8"));
        return bigint;
    }

    // Returns n shares of a given secret which can be reconstructed from any k
    // of them
    static String[] splitSecretIntoPieces(String secret, int n, int k)
            throws UnsupportedEncodingException
    {
        final BigInteger secretInteger;
        secretInteger = stringToBigInteger(secret);
        // secretInteger = BigInteger.valueOf(2L).pow(4096);
        final BigInteger modulus;
        modulus = secretInteger.nextProbablePrime(); // FAILS
        // modulus =
        // secretInteger.multiply(BigInteger.valueOf(32L)).nextProbablePrime(); // FAILS
        // modulus = secretInteger.multiply(secretInteger).nextProbablePrime(); // OK
        // modulus = SecretShare.getPrimeUsedFor384bitSecretPayload(); // OK
        // modulus = SecretShare.getPrimeUsedFor4096bigSecretPayload(); // OK
        //modulus = SecretShare.createAppropriateModulusForSecret(secretInteger); // OK
        final SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n,
                                                                             k,
                                                                             modulus,
                                                                             null);
        final SecretShare.SplitSecretOutput splitSecretOutput = new SecretShare(publicInfo)
                .split(secretInteger);
        final List<ShareInfo> pieces = splitSecretOutput.getShareInfos();
        String[] out = new String[pieces.size()];

        for (int i = 0; i < out.length; i++)
        {
            final ShareInfo piece = pieces.get(i);
            out[i] = n + ":" + k + ":" + piece.getX() + ":"
                    + publicInfo.getPrimeModulus() + ":" + piece.getShare();
        }
        return out;
    }

    private static ShareInfo newShareInfo(String piece)
    {
        String[] parts = piece.split(":");
        int i = 0;
        int n = Integer.parseInt(parts[i++]);
        int k = Integer.parseInt(parts[i++]);
        int x = Integer.parseInt(parts[i++]);
        BigInteger primeModulus = new BigInteger(parts[i++]);
        BigInteger share = new BigInteger(parts[i++]);
        if (!piece.equals("" + n + ":" + k + ":" + x + ":" + primeModulus + ":"
                + share))
        {
            throw new RuntimeException("Failed to parse " + piece);
        }
        return new ShareInfo(x, share, new SecretShare.PublicInfo(n,
                                                                  k,
                                                                  primeModulus,
                                                                  null));
    }

    static String mergePiecesIntoSecret(String[] pieces)
            throws UnsupportedEncodingException
    {
        final ShareInfo shareInfo = newShareInfo(pieces[0]);
        final SecretShare.PublicInfo publicInfo = shareInfo.getPublicInfo();
        final SecretShare solver = new SecretShare(publicInfo);
        final int k = publicInfo.getK();
        List<SecretShare.ShareInfo> kPieces = new ArrayList<SecretShare.ShareInfo>(k);
        kPieces.add(shareInfo);

        for (int i = 1; i < pieces.length && i < k; i++)
        {
            kPieces.add(newShareInfo(pieces[i]));
        }
        // EasyLinearEquationUT.enableLogging();
        SecretShare.CombineOutput solved = solver.combine(kPieces);
        BigInteger secret = solved.getSecret();
        return new String(secret.toByteArray(), "UTF-8");
    }
}

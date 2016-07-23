package com.tiemens.secretshare.engine;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.tiemens.secretshare.engine.SecretShare.CombineOutput;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;

public class GenerateMoreSharesTest {

    @Test
    public void testGenerateMoreShares() {
        final int n = 4;
        final int k = 2;
        final BigInteger secret = BigInteger.valueOf(12345L);

        Random random = new java.util.Random(123);
        BigInteger prime = SecretShare.getPrimeUsedFor192bitSecretPayload();
        SecretShare.PublicInfo publicInfo = new SecretShare.PublicInfo(n, k, prime, "");
        SecretShare secretShare = new SecretShare(publicInfo);
        SecretShare.SplitSecretOutput generate = secretShare.split(secret, random);
        List<SecretShare.ShareInfo> shares = generate.getShareInfos();

        assertEquals(n, shares.size());

        // recreate from secret
        SecretShare.PublicInfo combineInfo = new SecretShare.PublicInfo(n, k, prime, "");
        SecretShare combineShare = new SecretShare(combineInfo);

        SecretShare.ShareInfo one = shares.get(0);
        SecretShare.ShareInfo two = shares.get(1);
        List<SecretShare.ShareInfo> combineShares = new ArrayList<SecretShare.ShareInfo>();
        combineShares.add(one);
        combineShares.add(two);

        SecretShare.CombineOutput combine = combineShare.combine(combineShares);
        BigInteger reconstructedSecret = combine.getSecret();
        assertEquals(secret, reconstructedSecret);

        // re-generate more shares using just the combine:
        subtestRegenerate(shares, combine, combineInfo);
    }

    private void subtestRegenerate(List<ShareInfo> originalShares,
                                   CombineOutput combine,
                                   SecretShare.PublicInfo combineInfo)
    {
        combineInfo = SecretShare.PublicInfo.copyIncreaseNby(combineInfo, 5);
        SecretShare secretShare = new SecretShare(combineInfo);
        //SecretShare.SplitSecretOutput generate = secretShare.split(secret, random);
        BigInteger[] coeffs = combine.getCoeffs();
        SecretShare.SplitSecretOutput generate = secretShare.splitByCoeffsOnly(coeffs);
        List<SecretShare.ShareInfo> shares = generate.getShareInfos();

        assertEquals(combineInfo.getN(), shares.size());
        for (int i = 0, n = originalShares.size(); i < n; i++) {
            assertEquals(originalShares.get(i), shares.get(i));
        }

        for (int i = originalShares.size(), n = shares.size(); i < n; i++) {
            System.out.println("Generated new share: f(" + shares.get(i).getX() + ")=" + shares.get(i).getShare());
        }
    }

}

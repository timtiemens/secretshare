package com.tiemens.secretshare.main.cli;

import java.math.BigInteger;

import com.tiemens.secretshare.engine.SecretShare;
import com.tiemens.secretshare.engine.SecretShare.PublicInfo;
import com.tiemens.secretshare.engine.SecretShare.ShareInfo;
import com.tiemens.secretshare.engine.SecretShare.SplitSecretOutput;

/**
 * This is a COPY from the secretshareDown project.
 *
 */
public class SsDownMain {

	public static void main(String[] args) {
		BigInteger secret = new BigInteger("1234567890");
		BigInteger modulus = SecretShare.createAppropriateModulusForSecret(secret);
		PublicInfo publicInfo = new PublicInfo(6, 3, modulus, "Just Testing");
		SecretShare ss = new SecretShare(publicInfo);
		SplitSecretOutput split = ss.split(secret);

		String sep = "";
		for (ShareInfo shareInfo : split.getShareInfos()) {
			System.out.println(sep);
			sep = "\n\n";
			System.out.println(shareInfo.debugDump());
		}

	}

}

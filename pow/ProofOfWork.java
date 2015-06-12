package com.trestor.android.protocol.pow;

import com.trestor.android.protocol.address.AddressFactory;

import java.security.MessageDigest;

public class ProofOfWork {

	/**
	 * Specifies the number of zeros in increasing order
	 */
	static byte[] bitMask = new byte[] { (byte) 0xFF, (byte)0x7F, (byte)0x3F, (byte)0x1F, (byte)0x0F, (byte)0x07, (byte)0x03, (byte)0x01 }; 

    public static String CalculateProof(String source, int difficulty) throws Exception{
        byte[] sourceBytes = AddressFactory.hexStringToByteArray(source);
        byte[] outBytes = CalculateProof(sourceBytes, difficulty);
        String proofResult = AddressFactory.bytesToHex(outBytes);
        return proofResult;
    }

	/**
	 * Calculates a Work Proof based on a given Difficulty
	 * @param Initial
	 * @param Difficulty
	 * @return
	 * @throws Exception
	 */
	public static byte[] CalculateProof(byte[] Initial, int Difficulty) throws Exception
	{
		boolean Found = false;
		int InitialLength = Initial.length;

		int zeroBytes = Difficulty / 8;
		int zeroBits = Difficulty % 8;

		byte[] Content = new byte[InitialLength + 4];

		//Array.Copy(Initial, Content, InitialLength);
		//Content = Arrays.copyOf(Initial, InitialLength);
		System.arraycopy(Initial, 0, Content, 0, InitialLength);
		//UInt32 counter = 0;
		int counter = 0;

		if (zeroBytes < 30)
		{
			while (!Found)
			{
				byte[] CountBytes = BitConverter.getBytes(counter);  // Get bytes from counter

				System.arraycopy(CountBytes, 0, Content, InitialLength, 4); // Copy to the end.

				MessageDigest digest = (MessageDigest.getInstance("SHA-256"));
				byte[] Hash = digest.digest(digest.digest(Content)); // Calculate double hash

				boolean BytesGood = true;

				for (int i = 0; i < zeroBytes; i++)
				{
					if (Hash[i] != 0)
					{
						BytesGood = false;
						break;
					}
				}

				if (BytesGood)
				{
					if ((bitMask[zeroBits] | Hash[zeroBytes]) == bitMask[zeroBits])
					{
						Found = true;
						return BitConverter.getBytes(counter);
					}
				}

				counter++;
				
			}
		}

		throw new Exception("Proof Calculation Failure");
	}

}

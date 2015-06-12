package com.trestor.android.protocol.pow;

public class BitConverter {

	public static byte[] getBytes(int x)
	{
		return new byte[] {
				(byte)(x >>> 24),
				(byte)(x >>> 16),
				(byte)(x >>> 8),
				(byte)x
		};
	}

}

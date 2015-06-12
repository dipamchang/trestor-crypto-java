package com.trestor.android.protocol.encoding;

import org.apache.commons.lang3.ArrayUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class Base58Encoding {

    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
            .toCharArray();
    private static final int BASE_58 = ALPHABET.length;
    private static final int BASE_256 = 256;
    public static final int CheckSumSizeInBytes = 4;

    private static final int[] INDEXES = new int[128];

    static {
        for (int i = 0; i < INDEXES.length; i++) {
            INDEXES[i] = -1;
        }
        for (int i = 0; i < ALPHABET.length; i++) {
            INDEXES[ALPHABET[i]] = i;
        }
    }


    /**
     * Encode a byte array input to Base58 encodding
     * @param input
     * @return Nase58 encoded string
     */
    public static String encode(byte[] input) {
        if (input.length == 0) {
            // paying with the same coin
            return "";
        }

        //
        // Make a copy of the input since we are going to modify it.
        //
        input = copyOfRange(input, 0, input.length);

        //
        // Count leading zeroes
        //
        int zeroCount = 0;
        while (zeroCount < input.length && input[zeroCount] == 0) {
            ++zeroCount;
        }

        //
        // The actual encoding
        //
        byte[] temp = new byte[input.length * 2];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input.length) {
            byte mod = divmod58(input, startAt);
            if (input[startAt] == 0) {
                ++startAt;
            }

            temp[--j] = (byte) ALPHABET[mod];
        }

        //
        // Strip extra '1' if any
        //
        while (j < temp.length && temp[j] == ALPHABET[0]) {
            ++j;
        }

        //
        // Add as many leading '1' as there were leading zeros.
        //
        while (--zeroCount >= 0) {
            temp[--j] = (byte) ALPHABET[0];
        }

        byte[] output = copyOfRange(temp, j, temp.length);
        return new String(output);
    }

    /**
     * Decodes a Base58 string into a byte array
     * @param input
     * @return decoded byte array
     */
    public static byte[] decode(String input) {
        if (input.length() == 0) {
            // paying with the same coin
            return new byte[0];
        }

        byte[] input58 = new byte[input.length()];
        //
        // Transform the String to a base58 byte sequence
        //
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            int digit58 = -1;
            if (c >= 0 && c < 128) {
                digit58 = INDEXES[c];
            }
            if (digit58 < 0) {
                throw new RuntimeException("Not a Base58 input: " + input);
            }

            input58[i] = (byte) digit58;
        }

        //
        // Count leading zeroes
        //
        int zeroCount = 0;
        while (zeroCount < input58.length && input58[zeroCount] == 0) {
            ++zeroCount;
        }

        //
        // The encoding
        //
        byte[] temp = new byte[input.length()];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input58.length) {
            byte mod = divmod256(input58, startAt);
            if (input58[startAt] == 0) {
                ++startAt;
            }

            temp[--j] = mod;
        }

        //
        // Do no add extra leading zeroes, move j to first non null byte.
        //
        while (j < temp.length && temp[j] == 0) {
            ++j;
        }

        return copyOfRange(temp, j - zeroCount, temp.length);
    }

    private static byte divmod58(byte[] number, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number.length; i++) {
            int digit256 = (int) number[i] & 0xFF;
            int temp = remainder * BASE_256 + digit256;

            number[i] = (byte) (temp / BASE_58);

            remainder = temp % BASE_58;
        }

        return (byte) remainder;
    }

    private static byte divmod256(byte[] number58, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number58.length; i++) {
            int digit58 = (int) number58[i] & 0xFF;
            int temp = remainder * BASE_58 + digit58;

            number58[i] = (byte) (temp / BASE_256);

            remainder = temp % BASE_256;
        }

        return (byte) remainder;
    }

    private static byte[] copyOfRange(byte[] source, int from, int to) {
        byte[] range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);

        return range;
    }

    private static byte[] GetCheckSum(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash1 = digest.digest(data);
        byte[] hash2 = digest.digest(hash1);
        //TODO Why twice ?
        byte[] result = new byte[CheckSumSizeInBytes];
        result = Arrays.copyOfRange(hash2, 0, CheckSumSizeInBytes);
        return result;
    }

    public static byte[] AddCheckSum(byte[] data) throws NoSuchAlgorithmException {
        byte[] checkSum = GetCheckSum(data);
        byte[] dataWithCheckSum = ArrayUtils.addAll(data, checkSum);
        //TODO Remove usage of apache commons lang.
        return dataWithCheckSum;
    }

    public static String EncodeWithCheckSum(byte[] data) throws NoSuchAlgorithmException {
        //Contract.Requires<ArgumentNullException>(data != null);
        //Contract.Ensures(Contract.Result<string>() != null);
        return encode(AddCheckSum(data));
    }

    //Returns null if the checksum is invalid
    public static byte[] VerifyAndRemoveCheckSum(byte[] data) throws NoSuchAlgorithmException {
        byte[] result = ArrayUtils.subarray(data, 0, data.length - CheckSumSizeInBytes);
        //start inclusive , to exclisive
        byte[] givenCheckSum = ArrayUtils.subarray(data, data.length - CheckSumSizeInBytes, data.length);
        byte[] correctCheckSum = GetCheckSum(result);
        if (Arrays.equals(givenCheckSum, correctCheckSum))
            return result;
        else
            return null;
    }

    // Throws 'NumberFormatException' if s is not a valid Base58 string, or the checksum is invalid
    public static byte[] DecodeWithCheckSum(String s) throws NoSuchAlgorithmException {
        byte[] dataWithCheckSum = decode(s);
        byte[] dataWithoutCheckSum = VerifyAndRemoveCheckSum(dataWithCheckSum);
        if (dataWithoutCheckSum == null)
            throw new NumberFormatException("Base58 checksum is invalid");
        return dataWithoutCheckSum;
    }


}

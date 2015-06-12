package com.trestor.android.protocol.transaction;


public class Hash {

    private byte[] HashValue;

    /**
     * Constructor
     * @param hashvalue
     */
    public Hash(byte[] Value) {
        HashValue = Value;
    }

    /**
     * Constructor
     */
    public Hash() {
        HashValue = new byte[0];
    }

    /**
     *
     * @return hash value
     */
    public byte[] getHashValue() {
        return HashValue;
    }

    /**
     *
     * @return hash value
     */
    public byte[] Hex() {
        return HashValue;
    }

}

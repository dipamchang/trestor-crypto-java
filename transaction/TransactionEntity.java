package com.trestor.android.protocol.transaction;

import com.trestor.android.protocol.address.AccountIdentifier;

public class TransactionEntity {

    byte[] publicKey;
    String name;
    String address;
    long value;

    /**
     *
     * @return public key for the account
     */
    public byte[] getPublicKey() {
        return publicKey;
    }

    /**
     *
     * @return name for the account
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return address for the account
     */
    public String getAddress() {
        return address;
    }

    public long getValue() {
        return value;
    }

    /**
     * Constructor
     * @param account
     * @param value
     */
    public TransactionEntity(AccountIdentifier account, long value) {
        this.value = value;
        this.publicKey = account.PublicKey;
        this.name = account.Name;
        this.address = account.addressData.AddressString;
    }

}

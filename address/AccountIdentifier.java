package com.trestor.android.protocol.address;

import java.security.NoSuchAlgorithmException;

public class AccountIdentifier {
    public byte[] PublicKey;
    public String Name;
    public AddressData addressData;

    /**
     *
     * @return pk Public key of account
     */
    public byte[] getPublicKey() {
        return PublicKey;
    }

    /**
     *
     * @param publicKey set the public key of the account
     */
    public void setPublicKey(byte[] publicKey) {
        PublicKey = publicKey;
    }

    /**
     *
     * @return name Name of account
     */
    public String getName() {
        return Name;
    }


    /**
     *
     * @param name set the name of the account
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     *
     * @return address Address of account
     */
    public AddressData getAddressData() {
        return addressData;
    }

    /**
     *
     * @param address set the address of the account
     */
    public void setAddressData(AddressData addressData) {
        this.addressData = addressData;
    }

    /**
     * Class constructor
     * @param publicKey
     * @param name
     * @param addressDataString
     * @throws NoSuchAlgorithmException
     */
    public AccountIdentifier(byte[] publicKey, String name, String addressDataString) throws NoSuchAlgorithmException {
        this.PublicKey = publicKey;
        this.Name = name;
        this.addressData = AddressFactory.DecodeAddressString(addressDataString);

        if (!AddressFactory.VerfiyAddress(addressDataString, PublicKey, Name, addressData.getNetworkType(), addressData.getAccountType())) {
            throw new IllegalArgumentException("AccountIdentifier: Invalid Address Arguments");
        }
    }

}

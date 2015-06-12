package com.trestor.android.protocol.address;

import com.trestor.android.protocol.encoding.Base58Encoding;

import java.security.NoSuchAlgorithmException;

public class AddressData {
    public byte[] Address;
    public String AddressString;
    public NetworkType networkType;
    public AccountType accountType;

    /**
     *
     * @return Address of the associated account
     */
    public String getAddressString() {
        return AddressString;
    }

    public void setAddressString(String addressString) {
        AddressString = addressString;
    }

    /**
     *
     * @return Network type of the associated account
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    /**
     *
     * @return Account Type of the associated account
     */
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     *
     * @return Address of the associated account
     */
    public byte[] getAddress() {
        return Address;
    }

    public void setAddress(byte[] address) {
        Address = address;
    }

    /**
     * Constructor
     * @param Base58Address
     * @throws NoSuchAlgorithmException
     */
    public AddressData(String Base58Address) throws NoSuchAlgorithmException {
        byte[] add_data = Base58Encoding.DecodeWithCheckSum(Base58Address);

        if (add_data.length == 22) {
            AddressString = Base58Address;

            Address = new byte[20];

            System.arraycopy(add_data, 2, Address, 0, 20);

            //NetworkType = (NetworkType)add_data[0];
            networkType = NetworkType.getEnumNT(add_data[0]);
            accountType = AccountType.getEnumAT(add_data[1]);
        } else {
            throw new IllegalArgumentException("Invalid Decoded Address Length");
        }
    }

}

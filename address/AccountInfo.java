package com.trestor.android.protocol.address;


public class AccountInfo {

    AccountIdentifier account;
    byte[] secretSeed;

    /**
     *
     * @return Account data
     */
    public AccountIdentifier getAccount() {
        return account;
    }

    /**
     *
      * @param account Account Data
     */
    public void setAccount(AccountIdentifier account) {
        this.account = account;
    }

    /**
     *
     * @return Secret seed
     */
    public byte[] getSecretSeed() {
        return secretSeed;
    }

    /**
     *
     * @param secretSeed
     */
    public void setSecretSeed(byte[] secretSeed) {
        this.secretSeed = secretSeed;
    }

}

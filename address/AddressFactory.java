package com.trestor.android.protocol.address;

import com.trestor.android.protocol.crypto.Ed25519;
import com.trestor.android.protocol.encoding.Base58Encoding;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author dipamchang
 */

/*
 * Generate addresses from PublicKey and UserName
 */
public class AddressFactory {

    NetworkType _NetworkType;
    AccountType _AccountType;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    /**
     * Constructor
     * @param networkType
     * @param accountType
     */
    public AddressFactory(NetworkType networkType, AccountType accountType) {
        _NetworkType = networkType;
        _AccountType = accountType;
    }

    /**
     *
     * @return NetworkType
     */
    public NetworkType get_NetworkType() {
        return _NetworkType;
    }

    /**
     *
     * @param NetworkType
     */
    public void set_NetworkType(NetworkType _NetworkType) {
        this._NetworkType = _NetworkType;
    }

    /**
     *
     * @return AccounType
     */
    public AccountType get_AccountType() {
        return _AccountType;
    }

    /**
     *
     * @param AccountType
     */
    public void set_AccountType(AccountType _AccountType) {
        this._AccountType = _AccountType;
    }

    /**
     * Creates a new account and returns a Tuple containing account information and SecretSeed
     * @param Name
     * @param networkType
     * @return
     * @throws Exception
     */
    public static AccountInfo CreateNewAccount(String Name, NetworkType networkType) throws Exception {

        byte[] PrivateSecretSeed = new byte[32];

        SecureRandom sr = new SecureRandom();
        sr.nextBytes(PrivateSecretSeed);

        byte[] PublicKey = new byte[32];
        byte[] SecretKeyExpanded = new byte[64];

        Ed25519.KeyPairFromSeed(PublicKey, SecretKeyExpanded, PrivateSecretSeed);

        byte[] Address = GetAddress(PublicKey, Name, networkType, AccountType.MainNormal);

        String ADD = Base58Encoding.EncodeWithCheckSum(Address);
        AccountInfo accInformation = new AccountInfo();
        accInformation.setAccount(new AccountIdentifier(PublicKey, Name, ADD));
        accInformation.setSecretSeed(PrivateSecretSeed);
        return accInformation;
    }



    public static AccountIdentifier PrivateKeyToAccount(byte[] PrivateSecretSeed) throws Exception {
        return PrivateKeyToAccount(PrivateSecretSeed, "");
    }

    public static AccountIdentifier PrivateKeyToAccount(byte[] PrivateSecretSeed, String Name) throws Exception {
        byte[] PublicKey = null;
        byte[] SecretKeyExpanded = null;
        Ed25519.KeyPairFromSeed(PublicKey, SecretKeyExpanded, PrivateSecretSeed);
        return PublicKeyToAccount(PublicKey, Name);
    }

    public static AccountIdentifier PublicKeyToAccount(byte[] PublicKey) throws NoSuchAlgorithmException {
        return PublicKeyToAccount(PublicKey, "");
    }

    public static AccountIdentifier PublicKeyToAccount(byte[] PublicKey, String Name) throws NoSuchAlgorithmException {
        byte[] Address = GetAddress(PublicKey, Name, NetworkType.MainNet, AccountType.MainNormal);
        String ADD = Base58Encoding.EncodeWithCheckSum(Address);
        return new AccountIdentifier(PublicKey, Name, ADD);
    }

    public static AddressData DecodeAddressString(String Base58Address) throws NoSuchAlgorithmException {
        return new AddressData(Base58Address);
    }

    public static AccountIdentifier CreateAccountIdentifier(byte[] publicKey, String name, String addressDataString) throws NoSuchAlgorithmException {
        return new AccountIdentifier(publicKey, name, addressDataString);
    }

    /**
     * Returns SHA512 hashed byte array of the given format
     * Address Format : Address = NetType || AccountType || [H(H(PK) || PK || NAME || NetType || AccountType)], Take first 20 bytes}
     * @param PublicKey
     * @param UserName
     * @param networkType
     * @param accountType
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] GetAddress(byte[] PublicKey, String UserName, NetworkType networkType, AccountType accountType) throws NoSuchAlgorithmException {
        //Contract.Requires<ArgumentException>(PublicKey != null);
        //Contract.Requires<ArgumentException>(UserName != null);
        //Contract.Requires<ArgumentException>(PublicKey.Length == 32, "Public key length must be 32 bytes.");
        //Contract.Requires<ArgumentException>(UserName.Length < 64, "Username length should be less than 64.");

        if (networkType == NetworkType.MainNet) {
            if (accountType == AccountType.TestGenesis ||
                    accountType == AccountType.TestValidator ||
                    accountType == AccountType.TestNormal) {
                throw new IllegalArgumentException("Invalid AccountType for the provided NetworkType.");
            }
        }

        byte[] NAME = UserName.getBytes(Charset.forName("ISO-8859-1"));

        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] Hpk = digest.digest(PublicKey);

        byte[] NA_Type = new byte[]{(byte) networkType.getQuantity(), (byte) accountType.getQuantity()};

        //byte[] Hpk__PK__NAME = Hpk.Concat(PublicKey).Concat(NAME).Concat(NA_Type).ToArray();
        //ArrayUtils.addAll(Hpk, PublicKey, NAME, NA_Type);

        byte[] Hpk__PK__NAME = new byte[Hpk.length + PublicKey.length + NAME.length + NA_Type.length];
        System.arraycopy(Hpk, 0, Hpk__PK__NAME, 0, Hpk.length);
        System.arraycopy(PublicKey, 0, Hpk__PK__NAME, Hpk.length, PublicKey.length);
        System.arraycopy(NAME, 0, Hpk__PK__NAME, PublicKey.length + Hpk.length, NAME.length);
        System.arraycopy(NA_Type, 0, Hpk__PK__NAME, PublicKey.length + Hpk.length + NAME.length, NA_Type.length);

        //byte[] H_Hpk__PK__NAME = (new SHA512Cng()).ComputeHash(Hpk__PK__NAME).Take(20).ToArray();

        byte[] H_Hpk__PK__NAME = ArrayUtils.subarray(digest.digest(Hpk__PK__NAME), 0, 20);


        byte[] Address_PH = new byte[22];

        Address_PH[0] = NA_Type[0];
        Address_PH[1] = NA_Type[1];
        System.arraycopy(H_Hpk__PK__NAME, 0, Address_PH, 2, 20);
        /* byte[] CheckSum = (new SHA512Managed()).ComputeHash(Address_PH, 0, 22).Take(4).ToArray();
             Array.Copy(CheckSum, 0, Address_PH, 22, 4);*/

        return Address_PH;
    }

    public byte[] GetAddress(byte[] PublicKey, String UserName) throws NoSuchAlgorithmException {
        return GetAddress(PublicKey, UserName, _NetworkType, _AccountType);
    }

    /**
     * Returns true if the address, is consistent with the provided UserName and PublicKey
     * @param Address Address without checksum
     * @param PublicKey 32 byte Public Key
     * @param UserName UserName / can be zero length.
     * @return boolean
     * @throws NoSuchAlgorithmException
     */
    public boolean VerfiyAddress(byte[] Address, byte[] PublicKey, String UserName) throws NoSuchAlgorithmException {
        if (Arrays.equals(Address, GetAddress(PublicKey, UserName)))

        {
            return true;
        }

        return false;
    }

    public boolean VerfiyAddress(String Address, byte[] PublicKey, String UserName) throws NoSuchAlgorithmException {
        if (Address.equals(Base58Encoding.EncodeWithCheckSum(GetAddress(PublicKey, UserName))))
            return true;

        return false;
    }

    public static boolean VerfiyAddress(String Address, byte[] PublicKey, String UserName, NetworkType networkType, AccountType accountType) throws NoSuchAlgorithmException {
        if (Address.equals(Base58Encoding.EncodeWithCheckSum(GetAddress(PublicKey, UserName, networkType, accountType)))) {
            return true;
        }

        return false;
    }

    public static String GetAddressString(byte[] Address) throws NoSuchAlgorithmException {
        if (Address.length != 22) {
            throw new IllegalArgumentException("Invalid Address Length. Must be 22 bytes");
        } else {
            return Base58Encoding.EncodeWithCheckSum(Address);
        }
    }

    /**
     * Returns a hexadecimal string of input byte array
     * @param s hexadecimal string
     * @return byte array of hex decoded string
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Returns a hexadecimal string of input byte array
     * @param bytes
     * @return hex string
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}






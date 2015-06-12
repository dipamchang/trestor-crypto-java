package com.trestor.android.protocol.transaction;

import com.trestor.android.protocol.crypto.Ed25519;

import org.apache.commons.lang3.ArrayUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class TransactionContent {

    public Hash intTransactionID;

    public long timeStamp;
    public long transactionFee = 0;
    public byte[] versionData = {0, 0};
    public byte[] executionData = {};

    public List<TransactionEntity> Sources;
    public List<TransactionEntity> Destinations;

    public List<Hash> Signatures;

    /**
     *
     * @return timstamp of the transaction
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     *
     * @return total value associated with the transaction
     */
    public long getValue() {
        long val = 0;
        for (TransactionEntity te : Sources)
            val += te.value;

        return val;

    }

    /**
     *
     * @return list of sources for the transaction
     */
    public List<TransactionEntity> getSources() {
        return Sources;
    }

    /**
     *
     * @return list of destinations for the transaction
     */
    public List<TransactionEntity> getDestinations() {
        return Destinations;
    }

    void Init() {
        Sources = new ArrayList<TransactionEntity>();
        Destinations = new ArrayList<TransactionEntity>();
        Signatures = new ArrayList<Hash>();
        timeStamp = 0;
        transactionFee = 0;
        intTransactionID = new Hash();
    }

    /**
     * Constructor
     * @param Sources array of sources for the transaction
     * @param Destinations array of destination for the transaction
     * @param TransactionFee transaction fee for the transaction
     * @param Timestamp transaction for the transaction
     */
    public TransactionContent(TransactionEntity[] Sources, TransactionEntity[] Destinations, long TransactionFee, long Timestamp) {
        this.Destinations = Arrays.asList(Destinations);
        this.Sources = Arrays.asList(Sources);
        this.transactionFee = TransactionFee;
        this.timeStamp = Timestamp;
    }

    /**
     * Sets the signatures manually
     * Updates the transaction ID after hashing
     * @param Signatures
     * @throws NoSuchAlgorithmException
     */
    public void SetSignatures(List<Hash> Signatures) throws NoSuchAlgorithmException {
        this.Signatures = Signatures;
        UpdateIntHash();
    }

    /**
     *
     * @return transactionData transaction data which needs to be signed by all the individual sources
     */
    public byte[] getTransactionData() {

        List<Byte> transactionData = new ArrayList<Byte>();

        addRange(transactionData, versionData);
        addRange(transactionData, executionData);

        for (TransactionEntity ts : Sources) {
            addRange(transactionData, ts.getPublicKey());
            addRange(transactionData, Int64ToVector(ts.getValue()));
        }

        for (TransactionEntity ds : Destinations) {
            addRange(transactionData, ds.getPublicKey());
            addRange(transactionData, Int64ToVector(ds.getValue()));
        }

        // Adding Fee
        addRange(transactionData, Int64ToVector(transactionFee));

        // Adding Timestamp
        addRange(transactionData, Int64ToVector(timeStamp));

        return ListToArray(transactionData);

    }

    /**
     *
     * @param transactionData
     * @param data
     */
    public void addRange(List<Byte> transactionData, byte[] data) {
        for (int i = 0; i < data.length; i++) {
            transactionData.add(data[i]);
        }
    }

    public static byte[] Int64ToVector(long data) {
        byte[] _out = new byte[8];

        for (int i = 0; i < 8; i++) {
            _out[i] = (byte) (((data >>> (8 * i)) & 0xFF));
        }

        return _out;
    }

    /**
     * Converts a list of bytes to an array of bytes
     * @param bytes list of bytes
     * @return corrresponding array of bytes
     */
    public byte[] ListToArray(List<Byte> bytes) {
        byte[] ret = new byte[bytes.size()];
        Iterator<Byte> iterator = bytes.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().byteValue();
        }
        return ret;
    }

    /**
     * Checks the general integrity of transaction. Does not guarantee signatures are valid
     * @return boolean if transaction is valid or not
     */
    public TransactionProcessingResult IntegrityCheck() {
        long incoming = 0;
        long outgoing = 0;

        // TODO: MxN complexity, fix with loop limit or Dictionary.
        for (TransactionEntity src : Sources) {
            for (TransactionEntity dst : Destinations) {
                if (src.getAddress() == dst.getAddress())
                    return TransactionProcessingResult.SourceDestinationRepeat;
            }
        }

        if (Sources.size() != Signatures.size())
            return TransactionProcessingResult.InsufficientSignatureCount;

        for (TransactionEntity te : Sources) {
            if (te.getValue() <= 0)
                return TransactionProcessingResult.NoProperSources;

            incoming += te.getValue();
        }

        for (TransactionEntity te : Destinations) {
            if (te.getValue() <= 0)
                return TransactionProcessingResult.NoProperDestinations;

            outgoing += te.getValue();
        }

        outgoing += transactionFee;

        if ((incoming == outgoing) &&
                (Sources.size() > 0) &&
                (Destinations.size() > 0)) {
            return TransactionProcessingResult.Accepted;
        } else {
            return TransactionProcessingResult.InsufficientFunds;
        }

    }

    // Verifies all the signatures for all the sources.
    public TransactionProcessingResult VerifySignature() throws Exception {
        TransactionProcessingResult tp_result = IntegrityCheck();

        if (tp_result != TransactionProcessingResult.Accepted) {
            return tp_result;
        }

        byte[] transactionData = getTransactionData();

        // Adding Sources

        int PassedSignatures = 0;

        for (int i = 0; i < Sources.size(); i++) {
            TransactionEntity ts = Sources.get(i);

            boolean good = Ed25519.Verify(Signatures.get(i).getHashValue(), transactionData, Sources.get(i).getPublicKey());

            if (good) {
                PassedSignatures++;
            } else {
                return TransactionProcessingResult.SignatureInvalid;
            }
        }

        if (PassedSignatures == Sources.size()) {
            return TransactionProcessingResult.Accepted;
        } else {
            return TransactionProcessingResult.InsufficientSignatureCount; // Kindof redundant / #THINK
        }
    }

    /**
     *
     * @returns transaction dta and signatures
     */
    public byte[] GetTransactionDataAndSignature() {
        ArrayList<Byte> tranDataSig = new ArrayList<Byte>();

        byte[] tranData = getTransactionData();
        addRange(tranDataSig, tranData);

        for (Hash sig : Signatures)
            addRange(tranDataSig, sig.getHashValue());


        // Not performing Hashing Here, as it is already performed by ED25519.Sign()
        return ListToArray(tranDataSig);
    }

    /**
     * Updates the transaction id
     * @throws NoSuchAlgorithmException
     */
    private void UpdateIntHash() throws NoSuchAlgorithmException {
        byte[] tranDataSig = GetTransactionDataAndSignature();

        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] output = ArrayUtils.subarray(digest.digest(tranDataSig), 0, 32);

        intTransactionID = new Hash(output);
    }
}

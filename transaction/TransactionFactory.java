package com.trestor.android.protocol.transaction;

import com.trestor.android.protocol.address.AccountIdentifier;

import java.util.Collections;


public class TransactionFactory {

    AccountIdentifier source, destination;
    public long destValue, transactionFee = 0;

    public TransactionContent TC;

    byte[] tranxData;

    /**
     * Created new transaction data based on the given paramaters
     * Timestamp ofm transaction is set to the current time
     * @param source
     * @param destination
     * @param transactionFee
     * @param destValue
     */
    public TransactionFactory(AccountIdentifier source, AccountIdentifier destination, long transactionFee, long destValue) {
        this.source = source;
        this.destination = destination;
        this.destValue = destValue;
        this.transactionFee = transactionFee;

        TransactionEntity teSrc = new TransactionEntity(this.source, destValue + transactionFee);
        TransactionEntity teDst = new TransactionEntity(this.destination, destValue);

        long timeUtc = System.currentTimeMillis();

        //Convert Unix Timestamp to windows timestamp
        long timeWindows = (timeUtc * 10000L) + 116444736000000000L;
        TC = new TransactionContent(new TransactionEntity[]{teSrc}, new TransactionEntity[]{teDst}, transactionFee, timeWindows);

        tranxData = TC.getTransactionData();
    }

    /**
     * Use signature data, with the transaction data to create TransactionContent
     * @param signature
     * @return
     * @throws Exception
     */
    public TransactionProcessingResult Create(Hash signature) throws Exception {
        TC.SetSignatures(Collections.singletonList(signature));
        return TC.VerifySignature();

    }
}


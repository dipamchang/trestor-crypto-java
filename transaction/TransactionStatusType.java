package com.trestor.android.protocol.transaction;


public enum TransactionStatusType {
    Unprocessed(0),Proposed(17), InPreProcessing(18), InProcessingQueue(19), Processed(64), VoteInProgress(20), Success(80), Failure(32);

    int quantity;

    private TransactionStatusType(int q) {
        quantity = q;
    }

    int getQuantity() {
        return quantity;
    }

    public static TransactionStatusType get(int q) {
        TransactionStatusType nt = null;
        for (TransactionStatusType n : TransactionStatusType.values()) {
            if (n.getQuantity() == q) {
                nt = n;
                break;
            }

        }
        return nt;
    }
}

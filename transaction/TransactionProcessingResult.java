package com.trestor.android.protocol.transaction;

/**
 *  Types of processed transactions
 */
public enum TransactionProcessingResult {

    Unprocessed(0,"The transaction is yet to be processed."),
    Accepted(1,"Initial integrity checks passed. Queued for further processing."),
    InsufficientFunds(2,"Insufficient funds in source"),
    SourceSinkValueMismatch(3,"The values in source and destination don't match"),
    SignatureInvalid(4,"Invalid signature"),
    InsufficientSignatureCount(5,"Source does not have associated signature"),
    InsufficientFees(6,"Insufficient network fees"),
    InvalidTime(7,"Proposal time for the transaction is invalid"),
    InvalidTransactionEntity(8,"Invalid Source/Destination Entity or Main/Test Net Mismatch"),
    NoProperSources(9,"Source has less number of tre's than network minimum for any transaction."),
    NoProperDestinations(10,"Destination has less number of tre's than network minimum for any transaction."),
    InvalidVersion(11,"Invalid transaction packet version"),
    InvalidExecutionData(12,"Invalid execution data"),
    SourceDestinationRepeat(13,"Source is same as destination"),
    Failed(14,"Failed"),
    PR_SourceDoesNotExist(32,"Source does not exist"),
    PR_BadAccountName(33,"Invalid / Banned account name in destination."),
    PR_BadAccountAddress(34,"Destination account address validation failure"),
    PR_BadAccountCreationValue(35,"Insufficient amount to create new account"),
    PR_BadAccountState(36,"Invalid Destination account state banned/disabled"),
    PR_BadTransactionFee(37,"Invalid transaction fee"),
    PR_BadInsufficientFunds(38,"Not enough funds in account"),
    PR_Validated(64,"Validated"),
    PR_Success(80,"The transaction is successfully processed");

    private int quantity;
    private String description;

    TransactionProcessingResult(int q,String description) {
        quantity = q;
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }
    public String getDescription(){
        return description;
    }
    public static TransactionProcessingResult get(int q) {
        TransactionProcessingResult nt = null;
        for (TransactionProcessingResult n : TransactionProcessingResult.values()) {
            if (n.getQuantity() == q) {
                nt = n;
                break;
            }

        }
        return nt;
    }

}

package com.trestor.android.protocol.address;

/**
 * Specifies the type of account
 */
enum AccountType {
    MainGenesis((byte) 201), MainValidator((byte) 234), MainNormal((byte) 217),
    TestGenesis((byte) 25), TestValidator((byte) 59), TestNormal((byte) 40);
    int quantity;

    AccountType(int q) {
        quantity = q;
    }

    int getQuantity() {
        return quantity;
    }

    /**
     * Returns the AccountType object based on the enum constants
     * @param q enum constant
     * @return
     */
    public static AccountType getEnumAT(int q) {
        AccountType at = null;
        for (AccountType n : AccountType.values()) {
            if (n.getQuantity() == q) {
                at = n;
                break;
            }

        }
        return at;
    }

}

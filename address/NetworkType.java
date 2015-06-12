package com.trestor.android.protocol.address;
public enum NetworkType {
    MainNet((byte)14), TestNet((byte)29);
    int quantity;

    private NetworkType(int q) {
        quantity = q;
    }

    int getQuantity() {
        return quantity;
    }

    public static NetworkType getEnumNT(int q) {
        NetworkType nt = null;
        for (NetworkType n : NetworkType.values()) {
            if (n.getQuantity() == q) {
                nt = n;
                break;
            }

        }
        return nt;
    }

    public static NetworkType getValidatedValueOf(String networkType){
        if(networkType.equals("MainNet")){
            return MainNet;
        } else return TestNet;
    }
}

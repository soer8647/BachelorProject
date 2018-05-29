package Crypto.Impl;

public class WOTSKeyPair{


    private WotsPrivateKey privateKey;
    private WotsPublicKey publicKey;

    public WOTSKeyPair(WotsPrivateKey privateKey, WotsPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public WotsPublicKey  getPublicKey() {
        return publicKey;
    }

    public WotsPrivateKey getPrivateKey() {
        return privateKey;
    }
}

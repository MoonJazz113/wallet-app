package org.example.wallet.exception;

public class WalletNoCash extends RuntimeException {
    public WalletNoCash(String message) {
        super(message);
    }
}

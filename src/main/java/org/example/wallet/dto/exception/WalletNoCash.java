package org.example.wallet.dto.exception;

public class WalletNoCash extends RuntimeException {
    public WalletNoCash(String message) {
        super(message);
    }
}

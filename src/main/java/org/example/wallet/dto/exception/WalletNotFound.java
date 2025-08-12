package org.example.wallet.dto.exception;

public class WalletNotFound extends RuntimeException {
    public WalletNotFound(String message) {
        super(message);
    }
}

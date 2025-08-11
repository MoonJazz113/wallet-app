package org.example.wallet.service;

import org.example.wallet.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface WalletService {
    void performOperation(WalletOperationRequest request);
    WalletBalanceResponse getBalance(UUID walletId);
}
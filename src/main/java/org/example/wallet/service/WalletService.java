package org.example.wallet.service;

import org.example.wallet.dto.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface WalletService {
    void performOperation(WalletOperationRequest request);
    WalletBalanceResponse getBalance(UUID walletId);
}
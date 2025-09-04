package org.example.wallet.service.impl;

import jakarta.transaction.Transactional;
import org.example.wallet.repository.WalletRepository;
import org.example.wallet.dto.WalletBalanceResponse;
import org.example.wallet.dto.WalletOperationRequest;
import org.example.wallet.dto.exception.WalletNoCash;
import org.example.wallet.dto.exception.WalletNotFound;
import org.example.wallet.service.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    public void performOperation(WalletOperationRequest request) {
        var walletEntity = walletRepository.findById(request.walletId()).orElseThrow(() -> new WalletNotFound("Wallet not found"));
        BigDecimal amount = request.amount();
        BigDecimal balance = walletEntity.getBalance();

        switch (request.operationType()) {
            case DEPOSIT -> walletEntity.setBalance(balance.add(amount));
            case WITHDRAW -> {
                if (balance.compareTo(amount) < 0) {
                    throw new WalletNoCash("Insufficient funds");
                }
                walletEntity.setBalance(balance.subtract(amount));
            }
        }
        walletRepository.save(walletEntity);
    }

    @Override
    @Transactional
    public WalletBalanceResponse getBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(wallet -> new WalletBalanceResponse(wallet.getId(), wallet.getBalance()))
                .orElseThrow(() -> new WalletNotFound("Wallet not found"));
    }
}

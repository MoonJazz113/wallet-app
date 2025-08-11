package org.example.wallet.controller;


import org.example.wallet.dto.WalletBalanceResponse;
import org.example.wallet.dto.WalletOperationRequest;
import org.example.wallet.impl.WalletServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletServiceImpl walletService;

    public WalletController(WalletServiceImpl walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Void> updateBalance(@RequestBody WalletOperationRequest request) {
        walletService.performOperation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{walletId}")
    public WalletBalanceResponse getBalance(@PathVariable UUID walletId) {
        return walletService.getBalance(walletId);
    }
}

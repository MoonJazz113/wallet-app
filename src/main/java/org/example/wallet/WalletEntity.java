package org.example.wallet;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallets")
public class WalletEntity {
    @Id
    private UUID id;

    private BigDecimal balance;

    public WalletEntity(){
    }
    public WalletEntity(UUID id, BigDecimal balance){
        this.id = id;
        this.balance = balance;
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

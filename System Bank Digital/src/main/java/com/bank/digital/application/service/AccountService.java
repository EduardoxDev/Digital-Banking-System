package com.bank.digital.application.service;

import com.bank.digital.domain.exception.AccountNotFoundException;
import com.bank.digital.domain.model.Account;
import com.bank.digital.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(String holderName, BigDecimal initialBalance) {
        Account account = Account.builder()
                .holderName(holderName)
                .balance(initialBalance)
                .build();
        return accountRepository.save(account);
    }

    public Account getAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account " + id + " not found"));
    }
}

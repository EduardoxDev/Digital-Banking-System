package com.bank.digital.application.service;

import com.bank.digital.domain.exception.AccountNotFoundException;
import com.bank.digital.domain.model.Account;
import com.bank.digital.domain.model.Transaction;
import com.bank.digital.domain.repository.AccountRepository;
import com.bank.digital.domain.repository.TransactionRepository;
import com.bank.digital.infrastructure.messaging.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

        private final AccountRepository accountRepository;
        private final TransactionRepository transactionRepository;
        private final KafkaEventPublisher eventPublisher;

        @Transactional
        public void transfer(UUID sourceId, UUID destinationId, BigDecimal amount) {
                Account source = accountRepository.findById(sourceId)
                                .orElseThrow(() -> new AccountNotFoundException(
                                                "Source account " + sourceId + " not found"));
                Account destination = accountRepository.findById(destinationId)
                                .orElseThrow(() -> new AccountNotFoundException(
                                                "Destination account " + destinationId + " not found"));

                source.withdraw(amount);
                destination.deposit(amount);

                accountRepository.save(source);
                accountRepository.save(destination);

                Transaction transaction = Transaction.builder()
                                .sourceAccountId(sourceId)
                                .destinationAccountId(destinationId)
                                .amount(amount)
                                .type(Transaction.TransactionType.TRANSFER)
                                .timestamp(LocalDateTime.now())
                                .build();

                transactionRepository.save(transaction);

                // Publish event for Event Sourcing / Audit
                eventPublisher.publishTransactionEvent(transaction);
        }
}

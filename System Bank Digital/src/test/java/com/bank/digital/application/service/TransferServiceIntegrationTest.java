package com.bank.digital.application.service;

import com.bank.digital.AbstractIntegrationTest;
import com.bank.digital.domain.model.Account;
import com.bank.digital.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    private UUID accountAId;
    private UUID accountBId;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        Account a = Account.builder().holderName("Alice").balance(new BigDecimal("1000.00")).build();
        Account b = Account.builder().holderName("Bob").balance(new BigDecimal("1000.00")).build();
        accountAId = accountRepository.save(a).getId();
        accountBId = accountRepository.save(b).getId();
    }

    @Test
    void testSuccessfulTransfer() {
        transferService.transfer(accountAId, accountBId, new BigDecimal("100.00"));

        Account alice = accountRepository.findById(accountAId).orElseThrow();
        Account bob = accountRepository.findById(accountBId).orElseThrow();

        assertThat(alice.getBalance()).isEqualByComparingTo("900.00");
        assertThat(bob.getBalance()).isEqualByComparingTo("1100.00");
    }

    @Test
    void testConcurrentTransfersWithOptimisticLocking() throws InterruptedException {
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    transferService.transfer(accountAId, accountBId, new BigDecimal("10.00"));
                    successCount.incrementAndGet();
                } catch (ObjectOptimisticLockingFailureException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Account alice = accountRepository.findById(accountAId).orElseThrow();
        Account bob = accountRepository.findById(accountBId).orElseThrow();

        // total balance should remain 2000.00
        assertThat(alice.getBalance().add(bob.getBalance())).isEqualByComparingTo("2000.00");
        System.out.println("Successful transfers: " + successCount.get());
        System.out.println("Failed transfers due to locking: " + failureCount.get());
    }
}

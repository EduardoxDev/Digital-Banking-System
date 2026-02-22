package com.bank.digital.web;

import com.bank.digital.application.service.AccountService;
import com.bank.digital.application.service.TransferService;
import com.bank.digital.domain.model.Account;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BankingController {

    private final AccountService accountService;
    private final TransferService transferService;

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.ok(accountService.createAccount(request.getHolderName(), request.getInitialBalance()));
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @PostMapping("/transfers")
    public ResponseEntity<Map<String, String>> transfer(@Valid @RequestBody TransferRequest request) {
        transferService.transfer(request.getSourceId(), request.getDestinationId(), request.getAmount());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Transfer successful");
        return ResponseEntity.ok(response);
    }

    @Data
    public static class CreateAccountRequest {
        @NotNull(message = "Holder name is required")
        private String holderName;

        @NotNull(message = "Initial balance is required")
        @Positive(message = "Initial balance must be positive")
        private BigDecimal initialBalance;
    }

    @Data
    public static class TransferRequest {
        @NotNull(message = "Source account ID is required")
        private UUID sourceId;

        @NotNull(message = "Destination account ID is required")
        private UUID destinationId;

        @NotNull(message = "Amount is required")
        @Positive(message = "Transfer amount must be positive")
        private BigDecimal amount;
    }
}

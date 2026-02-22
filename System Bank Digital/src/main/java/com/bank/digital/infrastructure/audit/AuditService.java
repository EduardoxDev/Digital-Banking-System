package com.bank.digital.infrastructure.audit;

import com.bank.digital.domain.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditService {

    @KafkaListener(topics = "banking-transactions", groupId = "audit-group")
    public void auditTransaction(Transaction transaction) {
        log.info("[AUDIT] New Transaction Recorded: ID={} | From={} | To={} | Amount={} | Type={}",
                transaction.getId(),
                transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getAmount(),
                transaction.getType());
        
        // In a real system, this would be saved to a separate Audit DB or Elasticsearch
    }
}

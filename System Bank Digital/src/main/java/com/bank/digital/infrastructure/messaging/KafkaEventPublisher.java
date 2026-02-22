package com.bank.digital.infrastructure.messaging;

import com.bank.digital.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "banking-transactions";

    public void publishTransactionEvent(Transaction transaction) {
        log.info("Publishing transaction event to Kafka: {}", transaction.getId());
        kafkaTemplate.send(TOPIC, transaction.getId().toString(), transaction);
    }
}

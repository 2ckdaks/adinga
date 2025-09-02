package com.adinga.trigger_engine_service.config;

import com.adinga.trigger_engine_service.model.NotificationEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, NotificationEvent> producerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrap
    ) {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap
        ));
    }

    @Bean
    public KafkaTemplate<String, NotificationEvent> kafkaTemplate(
            ProducerFactory<String, NotificationEvent> pf
    ) {
        return new KafkaTemplate<>(pf);
    }
}

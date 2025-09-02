package com.adinga.notification_service.config;

import com.adinga.notification_service.model.NotificationEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    @Value("${spring.kafka.consumer.group-id:notification-service}")
    private String groupId;

    @Value("${app.topics.notifications-dlt:notifications.dlt}")
    private String notificationsDlt;

    // Consumer: NotificationEvent(JSON), type header 없이 수신
    @Bean
    public ConsumerFactory<String, NotificationEvent> notificationConsumerFactory() {
        JsonDeserializer<NotificationEvent> json = new JsonDeserializer<>(NotificationEvent.class);
        json.addTrustedPackages("*");
        json.setUseTypeHeaders(false);
        json.setRemoveTypeHeaders(true);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), json);
    }

    // DLT 발행용 Producer/Template (JSON으로 DLT에 적재)
    @Bean
    public ProducerFactory<String, NotificationEvent> notificationProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, NotificationEvent> notificationKafkaTemplate(
            ProducerFactory<String, NotificationEvent> pf
    ) {
        return new KafkaTemplate<>(pf);
    }

    @Bean(name = "notificationKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEvent>
    notificationKafkaListenerContainerFactory(
            ConsumerFactory<String, NotificationEvent> cf,
            KafkaTemplate<String, NotificationEvent> template
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, NotificationEvent>();
        factory.setConsumerFactory(cf);

        var recoverer = new DeadLetterPublishingRecoverer(
                template, (rec, ex) -> new TopicPartition(notificationsDlt, rec.partition())
        );
        factory.setCommonErrorHandler(new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L)));

        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        factory.setConcurrency(1);
        return factory;
    }
}

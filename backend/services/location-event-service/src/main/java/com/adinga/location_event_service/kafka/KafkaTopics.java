package com.adinga.location_event_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {

    @Bean
    public NewTopic locationEventsTopic(
            @Value("${app.kafka.location-topic:location-events}") String name // 기본값 추가
    ) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

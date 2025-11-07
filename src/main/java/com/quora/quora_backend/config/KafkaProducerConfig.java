package com.quora.quora_backend.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    // Generic ProducerFactory that works for any DTO (Object)
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // Kafka broker location
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Key serializer
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Value serializer (we send DTOs as JSON)
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // KafkaTemplate that can send any DTO (Object)
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

    /*KafkaTemplate → gives us an easy way to send messages in code, like
//with thi in your service you can just write==>
kafkaTemplate.send("topicName", myDtoObject); */

//why we made KafkaProducerConfig??
//Kafka doesn’t understand random Java objects,and we put it in config bcoz its configuration file,
//all setup things goes inside config(app setups things go here like kafka db etc)
//— it only understands bytes (tiny pieces of data). so we made producer
//ans-->Hey Spring, when I send data to Kafka, here’s how to connect 
//and how to convert my Java objects into JSON messages that Kafka can understand

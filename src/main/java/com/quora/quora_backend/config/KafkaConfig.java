package com.quora.quora_backend.config;

import com.quora.quora_backend.dto.AnswerEvent;
import com.quora.quora_backend.dto.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig; // <-- NEW IMPORT
import org.apache.kafka.clients.producer.ProducerConfig; // <-- NEW IMPORT
import org.apache.kafka.common.serialization.StringDeserializer; // <-- NEW IMPORT
import org.apache.kafka.common.serialization.StringSerializer; // <-- NEW IMPORT
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer; // <-- NEW IMPORT
import org.springframework.kafka.support.serializer.JsonSerializer; // <-- NEW IMPORT

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    public static final String NOTIFICATIONS_TOPIC = "notifications";

    // --- FACTORIES (Define how to connect) ---
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "quora-backend-group"); // Your group ID
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // Read only new messages
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.quora.quora_backend.dto"); // Trust our DTOs
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // --- BEAN 1: The Topic ---
    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name(NOTIFICATIONS_TOPIC).partitions(1).replicas(1).build();
    }

    // --- BEAN 2: The JSON "Mail Sorter" ---
    @Bean
    public RecordMessageConverter multiTypeConverter() {
        StringJsonMessageConverter converter = new StringJsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        Map<String, Class<?>> mappings = new HashMap<>();
        mappings.put("ANSWER_EVENT", AnswerEvent.class);
        mappings.put("COMMENT_EVENT", CommentEvent.class);
        typeMapper.setIdClassMapping(mappings);
        converter.setTypeMapper(typeMapper);
        return converter;
    }

    // --- BEAN 3: The Consumer "Mailroom" ---
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            RecordMessageConverter multiTypeConverter) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordMessageConverter(multiTypeConverter); // Use our sorter
        return factory;
    }

    // --- BEAN 4: The Producer "Mailman" ---
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory,
            RecordMessageConverter multiTypeConverter) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setMessageConverter(multiTypeConverter); // Use our sorter
        return kafkaTemplate;
    }
}
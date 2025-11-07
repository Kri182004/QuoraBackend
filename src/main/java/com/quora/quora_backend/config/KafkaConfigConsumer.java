package com.quora.quora_backend.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka //Tells Spring “we’re using Kafka in this app.” Without this, listeners won’t work.
@Configuration
public class KafkaConfigConsumer {
    @Bean
    public ConsumerFactory<String,Object> consumerFactory(){// ConsumerFactory,Think of it as the blueprint for creating Kafka consumers (listeners).
        Map<String,Object>configProps=new HashMap<>();
        //connect to kafka broker
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        //every consumer belongs to a group (imp for message sharing)//All consumers with the same group ID share messages — important in real-world apps.
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG,"quora-backend-group");
        //keys were serialized as strinf,so we deserilize them as string
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);//
        //value were serialized as json so we use jsonDeserializer//
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,JsonDeserializer.class);//converts the incoming JSON message back into your Java DTO object.
        //allow spring to trust the dto packages so it can safeky turn json back into objects
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES,"com.quora.quora_backend.dto");//Security feature — only allows deserializing objects from your package (prevents hacking).
    
        return new DefaultKafkaConsumerFactory<>(configProps);
}
@Bean
public ConcurrentKafkaListenerContainerFactory<String,Object>kafkaListenerContainerFactory(){
    ConcurrentKafkaListenerContainerFactory<String,Object>factory=
    new ConcurrentKafkaListenerContainerFactory<>();
factory.setConsumerFactory(consumerFactory());
return factory;
}
}/*ConcurrentKafkaListenerContainerFactory==>
This is what Spring uses behind @KafkaListener annotations to actually receive messages. */
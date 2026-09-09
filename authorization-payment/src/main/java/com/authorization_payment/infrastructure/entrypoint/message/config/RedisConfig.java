package com.authorization_payment.infrastructure.entrypoint.message.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    ReactiveRedisTemplate<String,String> redisTemplate(ReactiveRedisConnectionFactory factory){
        StringRedisSerializer serializer = new StringRedisSerializer();

        RedisSerializationContext<String,String> serializationContext = RedisSerializationContext
                .<String,String>newSerializationContext(serializer)
                .key(serializer)
                .value(serializer)
                .hashKey(serializer)
                .hashValue(serializer)
                .build();
        return new ReactiveRedisTemplate<>(factory,serializationContext);
    }
}

// package com.gateway.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
// import org.springframework.data.redis.core.RedisTemplate;

// @Configuration
// public class RedisConfig {

//     @Bean
//     public RedisTemplate<String, String> redisTemplate() {
//         RedisTemplate<String, String> template = new RedisTemplate<>();
//         template.setConnectionFactory(new LettuceConnectionFactory("localhost", 6379));
//         return template;
//     }
// }

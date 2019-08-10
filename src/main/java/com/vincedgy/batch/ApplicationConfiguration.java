package com.vincedgy.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class ApplicationConfiguration {

    @Bean
    LettuceConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory();
    }

}

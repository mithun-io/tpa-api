package com.tpa.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@Profile("test")
public class KafkaTestConfig {

    @Bean
    BeanPostProcessor disableKafkaListenersInTests() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof ConcurrentKafkaListenerContainerFactory<?, ?> factory) {
                    factory.setAutoStartup(false);
                }
                return bean;
            }
        };
    }
}

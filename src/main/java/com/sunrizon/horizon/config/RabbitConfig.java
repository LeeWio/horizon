package com.sunrizon.horizon.config;

import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.Resource;

import java.util.concurrent.Executor;

@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

  /**
   * Create a dedicated task executor for RabbitMQ listeners
   */
  @Bean(name = "rabbitTaskExecutor")
  public Executor rabbitTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("rabbit-");
    executor.initialize();
    return executor;
  }

  @Bean
  public MappingJackson2MessageConverter mappingJackson2MessageConverter() {
    return new MappingJackson2MessageConverter();
  }

  @Bean
  public MessageHandlerMethodFactory messageHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory defaultMessageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
    defaultMessageHandlerMethodFactory.setMessageConverter(mappingJackson2MessageConverter());
    return defaultMessageHandlerMethodFactory;
  }

  @Override
  public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
    registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
  }

  /**
   * Creates a custom RabbitTemplate with JSON message converter.
   * This ensures proper serialization/deserialization of messages.
   *
   * @param connectionFactory the RabbitMQ connection factory
   * @return configured RabbitTemplate
   */
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(new Jackson2JsonMessageConverter());
    return template;
  }

  /**
   * Creates a listener container factory with custom configuration.
   * This enables concurrent message processing and error handling.
   *
   * @param connectionFactory the RabbitMQ connection factory
   * @param rabbitTaskExecutor the dedicated executor for RabbitMQ
   * @return configured SimpleRabbitListenerContainerFactory
   */
  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory,
      @Qualifier("rabbitTaskExecutor") Executor rabbitTaskExecutor) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(new Jackson2JsonMessageConverter());
    factory.setConcurrentConsumers(3); // Number of consumer threads
    factory.setMaxConcurrentConsumers(10); // Maximum number of consumer threads
    factory.setTaskExecutor(rabbitTaskExecutor); // Use the dedicated rabbit executor
    return factory;
  }

}

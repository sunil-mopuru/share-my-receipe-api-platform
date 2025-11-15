package com.recipes.worker;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${recipe.queue.created}")
    private String createdQueue;

    @Value("${recipe.queue.updated}")
    private String updatedQueue;

    @Value("${recipe.queue.published}")
    private String publishedQueue;

    @Value("${recipe.queue.deleted}")
    private String deletedQueue;

    @Bean
    public TopicExchange recipeExchange() {
        return new TopicExchange("recipe.exchange");
    }

    @Bean
    public Queue createdQueue() {
        return new Queue(createdQueue, false);
    }

    @Bean
    public Queue updatedQueue() {
        return new Queue(updatedQueue, false);
    }

    @Bean
    public Queue publishedQueue() {
        return new Queue(publishedQueue, false);
    }

    @Bean
    public Queue deletedQueue() {
        return new Queue(deletedQueue, false);
    }

    @Bean
    public Binding createdBinding(Queue createdQueue, TopicExchange recipeExchange) {
        return BindingBuilder.bind(createdQueue).to(recipeExchange).with("recipe.created");
    }

    @Bean
    public Binding updatedBinding(Queue updatedQueue, TopicExchange recipeExchange) {
        return BindingBuilder.bind(updatedQueue).to(recipeExchange).with("recipe.updated");
    }

    @Bean
    public Binding publishedBinding(Queue publishedQueue, TopicExchange recipeExchange) {
        return BindingBuilder.bind(publishedQueue).to(recipeExchange).with("recipe.published");
    }

    @Bean
    public Binding deletedBinding(Queue deletedQueue, TopicExchange recipeExchange) {
        return BindingBuilder.bind(deletedQueue).to(recipeExchange).with("recipe.deleted");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
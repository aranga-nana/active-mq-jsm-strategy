package com.example.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class ActiveMqConfig
{


    @Value("${activemq.broker.url}")
    private String BROKER_URL;

    @Value("${activemq.username}")
    private String BROKER_USERNAME;

    @Value("${activemq.password}")
    private String BROKER_PASSWORD;

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setPassword(BROKER_PASSWORD);
        connectionFactory.setUserName(BROKER_USERNAME);
        RedeliveryPolicy policy =connectionFactory.getRedeliveryPolicy();
        policy.setDestination(new ActiveMQQueue("test.DLQ"));
        policy.setMaximumRedeliveries(2);



        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-5");
        return factory;
    }
    @Bean
    public ActiveMQQueue createQueue()
    {
        return new ActiveMQQueue("acnonline.worker.queue");
    }
}

package com.example.test;

import brave.Span;
import brave.Tracer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanName;
import org.springframework.cloud.sleuth.annotation.ContinueSpan;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class AccountProcessor {


    private Executor executor = Executors.newFixedThreadPool(20);

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private Tracer tracer;
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    @NewSpan("jms-submit")
    public boolean submit( String json)
    {
        jmsMessagingTemplate.convertAndSend(queue,json);

        return true;
    }

}

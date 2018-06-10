package com.example.test;

import brave.Span;
import brave.Tracer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanName;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AccountManager
{
    protected static Logger logger = LoggerFactory.getLogger(AccountManager.class);

    private ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private AccountProcessor processor;

    @Autowired
    private Tracer tracer;


    @NewSpan("create-account")
    public Map<String,Object> create(Long id)
    {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("status","success");

        map.put("traceId",tracer.currentSpan().context().traceId());
        map.put("spanId",tracer.currentSpan().context().spanId());
        map.put("parentId",tracer.currentSpan().context().parentId());


        String json = null;
        try
        {
            json = jsonMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        processor.submit(json);
        return map;
    }
}

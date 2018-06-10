package com.example.test;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Aspect
@Component
public class CustomSpan
{
    private static Logger logger = LoggerFactory.getLogger(CustomSpan.class);
    @Autowired
    private Tracer tracer;

    private ObjectMapper jsonMapp = new ObjectMapper();

    @Around("execution(* org.springframework.jms.core.JmsMessagingTemplate.*(..))")
    public Object processSend(ProceedingJoinPoint pjp) throws Throwable {

        return pjp.proceed();
    }
    //@Around("execution(* com.example.test.Worker.*(..))")
    @Around("@annotation(org.springframework.jms.annotation.JmsListener)")
    public Object inertSpan(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println(pjp.getArgs());
        Object[] a = pjp.getArgs();
        if (pjp.getArgs() == null || pjp.getArgs().length==0)
        {
            return pjp.proceed();
        }


        String json = (String)a[0];

        JsonNode node = jsonMapp.readValue(json,JsonNode.class);

        if (node.get("traceId") == null)
        {
            return pjp.proceed();
        }

        System.out.println(pjp.getSignature().getName());
        Long traceId = node.get("traceId").asLong();
        Long parentId = node.get("parentId").asLong();
        Long spanId = node.get("spanId").asLong();
        //Span span = tracer.nextSpan();
        TraceContext ctx= TraceContext.newBuilder().parentId(parentId).traceId(traceId).spanId(spanId).build();

        logger.info("done!");
        Span current = null;
        try
        {
            tracer.startScopedSpanWithParent(pjp.getSignature().getName(),ctx);
            current = tracer.currentSpan().name("jsm-received").tag("class",pjp.getTarget().getClass().getSimpleName()).tag("method",pjp.getSignature().getName()).annotate("jsm received");

            current.start();

            return pjp.proceed();
        }catch (Throwable e)
        {
            throw e;
        }
        finally {
            if (current != null)
            {
                current.annotate("jms processed").finish();
            }
        }


        // stop stopwatch

    }


}

package com.example.test;

import brave.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.SpanName;
import org.springframework.cloud.sleuth.annotation.ContinueSpan;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class Worker
{
    private  static Logger logger = LoggerFactory.getLogger(Worker.class);


    //@SpanName("recive-message")
    //@NewSpan()
    @JmsListener(destination = "acnonline.worker.queue")
    public void receiveQueue(String text) {


        logger.info(text);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

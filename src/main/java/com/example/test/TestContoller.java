package com.example.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanName;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class TestContoller
{

    @Autowired
    private AccountManager mgr;

    @PostMapping({"/",""})
    public Map<String,Object> create()
    {
        Map<String,Object> map = mgr.create(new Date().getTime());

        return map;
    }
}

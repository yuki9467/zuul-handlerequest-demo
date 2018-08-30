package com.example.zuuldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ZuuldemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuuldemoApplication.class, args);
    }
}

package com.aj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiruptorNettyClientApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DiruptorNettyClientApplication.class, args);
        new NettyClient().sendMessage();
    }

}

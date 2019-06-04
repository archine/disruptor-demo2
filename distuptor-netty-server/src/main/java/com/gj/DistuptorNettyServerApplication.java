package com.gj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistuptorNettyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistuptorNettyServerApplication.class, args);
        new NettyServer();
    }

}

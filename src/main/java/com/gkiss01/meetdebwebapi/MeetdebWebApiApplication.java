package com.gkiss01.meetdebwebapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MeetdebWebApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetdebWebApiApplication.class, args);
    }
}

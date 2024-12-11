package com.example.userservicedec24;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UserServiceDec24Application {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceDec24Application.class, args);
    }

}

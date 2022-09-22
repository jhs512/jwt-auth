package com.ll.exam.app868;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableJpaAuditing
public class App868Application {

    public static void main(String[] args) {
        SpringApplication.run(App868Application.class, args);
    }
}

package com.dqj.knowledgenavi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dqj.knowledgenavi"})
public class KnowledgeNaviApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgeNaviApplication.class, args);
    }

}

package com.dqj.knowledgenavi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dqj.knowledgenavi"})
@MapperScan("com.dqj.knowledgenavi.dao")
public class KnowledgeNaviApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgeNaviApplication.class, args);
    }

}

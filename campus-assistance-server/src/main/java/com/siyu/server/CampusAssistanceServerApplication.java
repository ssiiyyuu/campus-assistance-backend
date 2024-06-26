package com.siyu.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan({"com.siyu.shiro.mapper", "com.siyu.server.mapper", "com.siyu.common.mapper", "com.siyu.flowable.mapper"})
@ComponentScan(basePackages = {"com.siyu.shiro", "com.siyu.server", "com.siyu.common", "com.siyu.flowable", "com.siyu.oss", "com.siyu.websocket", "com.siyu.rabbitMQ"})
@SpringBootApplication
public class CampusAssistanceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusAssistanceServerApplication.class, args);
    }

}

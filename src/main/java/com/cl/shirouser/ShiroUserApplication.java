package com.cl.shirouser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cl.shirouser")
public class ShiroUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroUserApplication.class, args);
    }

}

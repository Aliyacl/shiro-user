package com.cl.shirouser;

import org.checkerframework.checker.signature.qual.BinaryNameForNonArrayInUnnamedPackage;
import org.elasticsearch.client.transport.TransportClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cl.shirouser")
public class ShiroUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroUserApplication.class, args);
    }

}

package com.bdd.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xing
 * @date 2018-09-27
 */
@ComponentScan("com.bdd")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GatewayApplication {

    /**
     * 程序入口
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
package com.wlf.order.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class OrderMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderMonitorApplication.class, args);
    }

}

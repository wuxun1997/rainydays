package com.wlf.order.prize;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class OrderPrizeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPrizeApplication.class, args);
    }

}

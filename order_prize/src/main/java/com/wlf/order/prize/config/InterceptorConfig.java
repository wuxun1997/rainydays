package com.wlf.order.prize.config;

import com.wlf.order.prize.aop.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor getRequestInterceptor() {
        return new RequestInterceptor();
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getRequestInterceptor()).addPathPatterns("/jpservice/ng/orders",
                "/jpservice/ng/order/status", "/jpservice/ng/order/address", "/jpservice/order/status").
                excludePathPatterns("/jpservice/getSign", "/jpservice/getTimeStamp");
    }
}

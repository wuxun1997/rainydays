package com.wlf.order.monitor.listen;

import com.wlf.order.monitor.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class EurekaListener {

    private final static SimpleDateFormat SF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    MailService mailService;

    @EventListener
    public void listen(EurekaInstanceCanceledEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("服务ID=");
        sb.append(event.getServerId());
        sb.append(System.getProperty("line.seperator", "\n"));
        sb.append("应用名=");
        sb.append(event.getAppName());
        sb.append(System.getProperty("line.seperator", "\n"));
        sb.append("时间=");
        sb.append(SF.format(event.getTimestamp()));

        mailService.sendSimpleMail("火烧眉毛！！！你的服务挂了，快来救火~~~", sb.toString());
    }
}

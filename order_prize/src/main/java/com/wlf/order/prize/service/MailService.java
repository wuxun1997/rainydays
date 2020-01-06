package com.wlf.order.prize.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.to}")
    private String mailTo;

    @Async
    public void sendSimpleMail(String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setCc(mailFrom);
        message.setTo(mailTo.contains(";") ? mailTo.split(";") : new String[]{mailTo});
        message.setSubject(subject);
        message.setText(content);

        try {
            javaMailSender.send(message);
            log.info("邮件已经发送。");
        } catch (Exception e) {
            log.error("发送邮件异常", e);
        }

    }

}

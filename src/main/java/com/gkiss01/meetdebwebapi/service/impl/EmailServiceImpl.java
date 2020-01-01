package com.gkiss01.meetdebwebapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender javaMailSender;

    void sendConfirmationMessage(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("MeetDeb - Complete Registration!");
        message.setFrom("meetdeb@unideb.hu");
        message.setText("To confirm your account, please click here:\n" + token);
        sendEmail(message);
    }

    @Async
    void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }
}
package com.javastart.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javastart.notification.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class DepositMessageHandler {

    private final JavaMailSender javaMailSender;

    @Autowired
    public DepositMessageHandler(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DEPOSIT)
    public void receive(Message message) throws JsonProcessingException {
        System.out.println(message);
        byte[] body = message.getBody();
        String jsonBody = new String(body);
        ObjectMapper objectMapper = new ObjectMapper();
        DepositResponceDTO depositResponceDTO = objectMapper.readValue(jsonBody, DepositResponceDTO.class);
        System.out.println(depositResponceDTO);


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(depositResponceDTO.getMail());
        mailMessage.setFrom("lori@cat.xyz");


        mailMessage.setSubject("Deposit");
        mailMessage.setText("Make deposit, sum:" + depositResponceDTO.getAmount());

        try {
            javaMailSender.send(mailMessage);
        } catch (Exception exception) {
            System.out.println(exception);
        }

    }
}

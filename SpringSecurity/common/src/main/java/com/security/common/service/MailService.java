package com.security.common.service;

import freemarker.core.ParseException;
import freemarker.template.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MailService {

    @Autowired
    JavaMailSenderImpl mailSender;

    @Autowired
    private Configuration configuration;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);


    public boolean sendMail(String to, int otp, String mailTemplate,Optional<String> optionalSubject, String firstname) {
        final Map<String, Object> map = new HashMap<>();
        if (otp != 0) {
            map.put("otp",Integer.toString(otp));
            map.put("username", to);
            map.put("name", firstname);
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        try {
            Template template = configuration.getTemplate(mailTemplate);
            String mailContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            String subject = "Mail from Security";
            if(optionalSubject.isPresent()){
                subject = optionalSubject.get();
            }
            messageHelper.setSubject(subject);
            messageHelper.setFrom("vermashubham244@gmail.com");
            messageHelper.setTo(to);
            messageHelper.setText(mailContent, true);
            mailSender.send(message);
            LOGGER.info("An email to {} has been sent successfully.", to);

            return true;

        }catch (MessagingException | IOException | TemplateException e) {
            LOGGER.error("An error occurred while sending an email.", e);
            return false;
        }

    }
}

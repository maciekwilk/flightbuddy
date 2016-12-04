package com.flightbuddy.mails;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.flightbuddy.results.FoundTrip;

@Service
public class MailService {

	Logger log = LoggerFactory.getLogger(MailService.class);
	
	@Value("${mail.address}")
	private String emailAddress;
	@Autowired
    private JavaMailSender javaMailSender;
	
	public void sendMail(List<FoundTrip> foundTrips) {
        MimeMessage mail = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
	        helper.setTo(emailAddress);
	        helper.setFrom(emailAddress);
	        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
	        String message = "";
	        for (FoundTrip foundTrip : foundTrips) {
	    		try {
	    			message += writer.writeValueAsString(foundTrip);
	    		} catch(JsonProcessingException e) {
	    			log.error(e.getMessage());
	    		}
	        }
	        helper.setText(message);
        } catch (MessagingException e) {
        	log.error(e.getMessage());
        }
        this.javaMailSender.send(mail);
	}

}

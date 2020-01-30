package com.flightbuddy.mails;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.flightbuddy.resources.Messages;
import com.flightbuddy.results.FoundTrip;

@Service
@PreAuthorize("hasRole('SYSTEM')")
public class MailService {

	private static final Logger log = LoggerFactory.getLogger(MailService.class);
	
	@Value("${mail.address}")
	private String emailAddress;
	
	@Autowired
    private JavaMailSender javaMailSender;
	@Autowired
	private MessageWriter messageWriter;
	
	public void sendTrips(List<FoundTrip> foundTrips) {
		String message = messageWriter.prepareMessage(foundTrips);
		String subject = Messages.get("mail.trips.subject");
		sendMail(emailAddress, emailAddress, subject, message);
	}
	
	private void sendMail(String from, String to, String subject, String message) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        send(mailMessage);
	}

	private void send(SimpleMailMessage mailMessage) {
		try {
            javaMailSender.send(mailMessage);
            log.info(Messages.get("mail.success", mailMessage.getFrom(), mailMessage.getTo()[0], mailMessage.getSubject()));
        } catch (MailException ex) {
            log.error(Messages.get("error.mail.sending", mailMessage.getFrom(), mailMessage.getTo()[0], mailMessage.getSubject()), ex);
        }
	}

}

package org.cdm.web.backend.email;

public interface EmailService {
    public void sendSimpleMessage(String to, String subject, String text);
}

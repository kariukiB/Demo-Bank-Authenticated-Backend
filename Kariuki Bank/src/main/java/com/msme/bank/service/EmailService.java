package com.msme.bank.service;

import com.msme.bank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlerts(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}

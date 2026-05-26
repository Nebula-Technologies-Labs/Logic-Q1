package com.logicq1.crm.modules.email.dto;

import lombok.Builder;

@Builder
public record MailBody(String to , String subject , String text) {
}

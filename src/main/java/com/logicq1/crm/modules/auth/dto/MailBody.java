package com.logicq1.crm.modules.auth.dto;

import lombok.Builder;

@Builder
public record MailBody(String to , String subject , String text) {



}

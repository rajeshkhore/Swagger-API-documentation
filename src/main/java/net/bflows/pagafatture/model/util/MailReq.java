package net.bflows.pagafatture.model.util;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class MailReq {
    
    String senderEmail;
    String recevierEmail;
    Map<String, String> params;
    Long templateId;
    List<String> CC;
    List<String> BCC;
    List<String> recipients;
    String subject;
}

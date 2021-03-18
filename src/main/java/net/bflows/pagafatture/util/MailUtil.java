package net.bflows.pagafatture.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.model.util.MailReq;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.Status;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.SmtpApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailBcc;
import sibModel.SendSmtpEmailCc;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

@Component
public class MailUtil {
    
    @Value("${apiKey}")
    public String apiKeyValue;
    
    @Autowired
    private Environment environment;
    
    public void sendMail(MailReq mailReq) {

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(apiKeyValue);

        SmtpApi apiInstance = new SmtpApi();
        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        
        SendSmtpEmailSender sendSmtpEmailSender=new SendSmtpEmailSender();
        sendSmtpEmailSender.setEmail(mailReq.getSenderEmail());
        
        SendSmtpEmailTo sendSmtpEmailTo=new SendSmtpEmailTo();
        sendSmtpEmailTo.setEmail(mailReq.getRecevierEmail());
        
        sendSmtpEmail.sender(sendSmtpEmailSender);
        sendSmtpEmail.setTo(new ArrayList<SendSmtpEmailTo>(){{add(sendSmtpEmailTo);}});
        sendSmtpEmail.setTemplateId(mailReq.getTemplateId());
        sendSmtpEmail.setParams(mailReq.getParams());
        
        try {
            apiInstance.sendTransacEmail(sendSmtpEmail);
        } catch (ApiException e) {
            throw new CustomValidationException(Status.EXCEPTION_ERROR, Translator.toLocale("common_message"));
        }
    }
    
    
    
    public void sendMailForAction(MailReq mailReq) {

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        List<SendSmtpEmailTo> sendSmtpEmailToList=new ArrayList<>();
		List<SendSmtpEmailBcc> sendSmtpEmailBCCList=new ArrayList<>();
		List<SendSmtpEmailCc> sendSmtpEmailCCList=new ArrayList<>();
        
        ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(apiKeyValue);

        SmtpApi apiInstance = new SmtpApi();
        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        
        SendSmtpEmailSender sendSmtpEmailSender=new SendSmtpEmailSender();
        sendSmtpEmailSender.setEmail(mailReq.getSenderEmail());
       
		if (mailReq.getRecipients() != null) {
			for (String recipient : mailReq.getRecipients()) {
				SendSmtpEmailTo sendSmtpEmailTo = new SendSmtpEmailTo();
				sendSmtpEmailTo.setEmail(recipient);
				sendSmtpEmailToList.add(sendSmtpEmailTo);
			}
		}

		if (mailReq.getCC() != null) {
			for (String CC : mailReq.getCC()) {
				SendSmtpEmailCc sendSmtpEmailCc = new SendSmtpEmailCc();
				sendSmtpEmailCc.setEmail(CC);
				sendSmtpEmailCCList.add(sendSmtpEmailCc);
			}
		}

		if (mailReq.getBCC() != null) {
			for (String BCC : mailReq.getBCC()) {
			SendSmtpEmailBcc sendSmtpEmailBcc = new SendSmtpEmailBcc();
			sendSmtpEmailBcc.setEmail(BCC);
			sendSmtpEmailBCCList.add(sendSmtpEmailBcc);
			}
		}
        
		
		if(!CollectionUtils.isEmpty(sendSmtpEmailToList)) {
			sendSmtpEmail.setTo(sendSmtpEmailToList);
		}
		if(!CollectionUtils.isEmpty(sendSmtpEmailCCList)) {
			 sendSmtpEmail.setCc(sendSmtpEmailCCList);
		}
		if(!CollectionUtils.isEmpty(sendSmtpEmailBCCList)) {
			sendSmtpEmail.setBcc(sendSmtpEmailBCCList);
		}
		
        sendSmtpEmail.sender(sendSmtpEmailSender);
        sendSmtpEmail.setTemplateId(mailReq.getTemplateId());
        sendSmtpEmail.setParams(mailReq.getParams());
        sendSmtpEmail.setSubject(mailReq.getSubject());
        
        try {
            apiInstance.sendTransacEmail(sendSmtpEmail);
        } catch (ApiException e) {
            throw new CustomValidationException(Status.EXCEPTION_ERROR, Translator.toLocale("common_message"));
        }
    }
    
    
    
    public Boolean isTestProfileActivated() {
    	String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.stream(activeProfiles).anyMatch("test"::equals);
    }
}

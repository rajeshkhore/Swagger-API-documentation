package net.bflows.pagafatture.dao;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.entities.MerchantEntity;
import net.bflows.pagafatture.entities.RoleEntity;
import net.bflows.pagafatture.entities.RoleEntity.RoleEnum;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.entities.WorkflowEntity;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.model.UserReq.UserStatusEnum;
import net.bflows.pagafatture.model.UserUpdateReq;
import net.bflows.pagafatture.model.util.MailReq;
import net.bflows.pagafatture.repositories.RoleRepository;
import net.bflows.pagafatture.repositories.UserRepository;
import net.bflows.pagafatture.repositories.WorkflowRepository;
import net.bflows.pagafatture.util.DateTimeUtil;
import net.bflows.pagafatture.util.MailUtil;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.ResourceNotFoundException;
import net.bflows.pagafatture.web.rest.errors.Status;

@Component
public class UserDAO {
    public static final String INVALID_USER = "invalid_user";
    public static final String INVALID_ROLE = "invalid_role";


    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MailUtil mailUtil;
    
    @Value("${FE_BASE_URL}")
    public String feBaseURL;
    
    @Value("${senderEmail}")
    public String senderEmail;
    
    @Value("${validTokenTimeInMinute}")
    private Long validTokenTimeInMinute;
    
    @Autowired
    WorkflowRepository workflowRepository;
    
    @Value("${default_workflow_name}")
    private String defaultWorkflowName;
    
    @Value("${email_confirmation_url}")
    public String emailConfirmationURL;
    
    public final Logger log = LoggerFactory.getLogger(UserDAO.class);

    public UserEntity createUser(UserReq user) {

        UserEntity userEntity = new UserEntity();
        MerchantEntity merchantEntity=new MerchantEntity();
        
        if (user.getRoleId()!=null && !(user.getRoleId().equals(0l))) {
            Optional<RoleEntity> role = roleRepository.findById(user.getRoleId());
            if (!role.isPresent()) {
            	throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ROLE));
            }
            userEntity.setRole(role.get());
        } else {
            userEntity.setRole(roleRepository.findByRoleName(RoleEnum.ROLE_CUSTOMER.getValue()).get());
        }

        
        userEntity.setEmail(user.getEmail());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setPhone(user.getPhone());
        userEntity.setUserStatus(UserStatusEnum.ACTIVE.getValue());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        
        merchantEntity.setName(user.getCompanyName());
        merchantEntity.setUserEntity(userEntity);
        merchantEntity.setDefaultPaymentDays(60);
        userEntity.setMerchant(merchantEntity);
        userEntity = userRepository.save(userEntity);
        WorkflowEntity workflowEntity = new WorkflowEntity();
        workflowEntity.setDefaultWorkflow(true);
        workflowEntity.setName(defaultWorkflowName);
        workflowEntity.setIsDeleted(false);
        workflowEntity.setCreatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
        workflowEntity.setUpdatedDate(DateTimeUtil.DateServerToUTC(LocalDateTime.now()));
        workflowEntity.setMerchantEntity(userEntity.getMerchant());
        workflowRepository.save(workflowEntity);
       
        
       
        String generatedToken = generateToken();
        userEntity.setEmailToken(generatedToken);
        userRepository.save(userEntity);

        String linkToSend = feBaseURL+emailConfirmationURL+"?username=" + userEntity.getEmail() + "&token="
                + generatedToken.split(":")[0];
        Map<String, String> params = new HashMap<String, String>();

        params.put("link", linkToSend);
        params.put("name", userEntity.getFirstName());

        MailReq mailReq = new MailReq();
        mailReq.setTemplateId(11l);
        mailReq.setParams(params);
        mailReq.setRecevierEmail(userEntity.getEmail());
        mailReq.setSenderEmail(senderEmail);

        // Sending Mail to user
        if (Boolean.FALSE.equals(mailUtil.isTestProfileActivated())) {
			mailUtil.sendMail(mailReq);
		}
        
        return null;

    }
    
    public UserEntity updateUser(String username,UserUpdateReq user) {
        
        Optional<UserEntity>userEnt= userRepository.findByEmailIgnoreCase(username);
        if(!userEnt.isPresent()){
        	throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_USER));  
        }
        
        UserEntity userEntity=userEnt.get();
        
        if (user.getRoleId()!=null && !(user.getRoleId().equals(0l))) {
            Optional<RoleEntity> role = roleRepository.findById(user.getRoleId());
            if (!role.isPresent()) {
            	throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_ROLE));
            }
            userEntity.setRole(role.get());
        }
          userEntity.setFirstName(user.getFirstName());
          userEntity.setLastName(user.getLastName());
          userEntity.setPhone(user.getPhone());

          userEntity = userRepository.save(userEntity);

          return userEntity;

      }
    
    public UserEntity getUserByName(String userName) {

        Optional<UserEntity> user=userRepository.findByEmailIgnoreCase(userName);
        if(!user.isPresent()){
            throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_USER));
        }
        return user.get();

    }
    
    public Boolean changeUserPassword(String username, String password, String newPassword) {

        Optional<UserEntity> userEnt = userRepository.findByUserStatusAndEmailIgnoreCase(
                UserStatusEnum.ACTIVE.getValue(), username);
        if (!userEnt.isPresent()) {
        	throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_USER));
        }
        
        boolean resp = passwordEncoder.matches(password, userEnt.get().getPassword());
        if (!resp) {
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale("invalid_old_pass_msg"));
        }
        userEnt.get().setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(userEnt.get());

        return true;
    }

    public Boolean changeUserPasswordRequest(String username) {

        Optional<UserEntity> userEnt = userRepository.findByUserStatusAndEmailIgnoreCase(
                UserStatusEnum.ACTIVE.getValue(), username);
        if (!userEnt.isPresent()) {
        	throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_USER));
        }

        String generatedToken = generateToken();

        userEnt.get().setTempToken(generatedToken);
        userRepository.save(userEnt.get());

        String linkToSend = feBaseURL + "/change-password?username=" + userEnt.get().getEmail() + "&token="
                + generatedToken.toString().split(":")[0];
        Map<String, String> params = new HashMap<String, String>();

        params.put("link", linkToSend);
        params.put("name", userEnt.get().getFirstName());

        MailReq mailReq = new MailReq();
        mailReq.setTemplateId(9l);
        mailReq.setParams(params);
        mailReq.setRecevierEmail(userEnt.get().getEmail());
        mailReq.setSenderEmail(senderEmail);

        // Sending Mail to user
        if (Boolean.FALSE.equals(mailUtil.isTestProfileActivated())) {
			mailUtil.sendMail(mailReq);
		}

        return true;
    }

	private String generateToken() {
		StringBuilder generatedToken = new StringBuilder();
        SecureRandom number;
        try {
            number = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            throw new CustomValidationException(Status.EXCEPTION_ERROR, Translator.toLocale("common_message"));
        }

        for (int i = 1; i < 7; i++) {
            generatedToken.append(number.nextInt(9));
        }
        generatedToken.append(":");
        generatedToken.append(System.currentTimeMillis());
		return generatedToken.toString();
	}

    public Boolean resetUserPassword(String username, String token, String newPassword) {

        Optional<UserEntity> userEnt = userRepository.findByUserStatusAndEmailIgnoreCase(
                UserStatusEnum.ACTIVE.getValue(), username);
        if (!userEnt.isPresent()) {
        	throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_USER));
        }

        Long validMin = validTokenTimeInMinute;
        Long validSeconds = validMin * 60;

        String dbToken = userEnt.get().getTempToken();
        String[] dbTokenArray = dbToken.split(":");

        String generatedToken = dbTokenArray[0];
        String tokenGenerationTime = dbTokenArray[1];

        Long tokenGenerationTimeLongValue = Long.parseLong(tokenGenerationTime);
        Long tokenRecievedTime = System.currentTimeMillis();

        Long differenceTime = tokenRecievedTime - tokenGenerationTimeLongValue;
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(differenceTime);

        if (validSeconds.equals(seconds) || validSeconds > seconds) {

            if (generatedToken.equals(token)) {
                userEnt.get().setPassword(passwordEncoder.encode(newPassword));
                userEnt.get().setTempToken(null);
            } else {
                throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale("invalid_token_msg"));
            }
        } else {
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale("expire_token_msg"));
        }
        userRepository.save(userEnt.get());

        return true;
    }

	public String verifyEmail(String username, String token) {
		
		  Optional<UserEntity> userEnt = userRepository.findByUserStatusAndEmailIgnoreCase(
	                UserStatusEnum.ACTIVE.getValue(), username);
	        if (!userEnt.isPresent()) {
	        	throw new ResourceNotFoundException(Status.NOT_FOUND, Translator.toLocale(INVALID_USER));
	        }
	        UserEntity userEntity = userEnt.get();
	        if(Boolean.TRUE.equals(userEntity.getIsEmailVerified())) {
	        	 return Translator.toLocale("email_verified");
	        }
	        String dbToken = userEnt.get().getEmailToken();
	        String[] dbTokenArray = dbToken.split(":");

	        String generatedToken = dbTokenArray[0];

	        if(generatedToken.equals(token)) {
	           
	        	userEntity.setIsEmailVerified(true);
	        	userRepository.save(userEntity);
	        return Translator.toLocale("email_se_msg");
	        }else {
	        	 throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale("invalid_token_msg"));
	        }
	        
	        
	}
}

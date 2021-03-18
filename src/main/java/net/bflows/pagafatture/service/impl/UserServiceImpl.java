package net.bflows.pagafatture.service.impl;

import java.util.Optional;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.config.jwt.TokenProvider;
import net.bflows.pagafatture.dao.UserDAO;
import net.bflows.pagafatture.entities.RoleEntity.RoleEnum;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.model.UserUpdateReq;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.model.util.JWTToken;
import net.bflows.pagafatture.repositories.UserRepository;
import net.bflows.pagafatture.service.UserService;
import net.bflows.pagafatture.web.rest.errors.CustomValidationException;
import net.bflows.pagafatture.web.rest.errors.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    public static final String PASSWORD_MIN_LEN = "pass_min_len_msg";
    public static final String EMAIL_EXISTS = "email_exist";
    public static final String INVALID_PASSWORD = "invalid_password";


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    UserDAO userdao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity createUser(UserReq user) {

        // Request validations
        if(user.getPassword().length()< 8){
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(PASSWORD_MIN_LEN));
        }
        
        Optional<UserEntity> userOptional = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (userOptional.isPresent()) {
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(EMAIL_EXISTS));
        }

        if (!user.getPassword().equals(user.getConfPassword())) {
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(INVALID_PASSWORD));
        }
       
        // Calling DAO Function
        return userdao.createUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public JWTToken loginUser(String username, String password, Boolean rememberMe) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt=null;
        UserEntity user = userRepository.findByEmailIgnoreCase(username).get();
        if(user.getRole().getRoleName().equals(RoleEnum.ROLE_ADMIN.getValue()) ||
        		user.getRole().getRoleName().equals(RoleEnum.ROLE_SUPER_ADMIN.getValue())) {
      	   jwt = tokenProvider.createToken(authentication,null,rememberMe);
         }else {
      	   jwt = tokenProvider.createToken(authentication,user.getMerchant().getId(),rememberMe); 
         }
        if(user.getRole().getRoleName().equals(RoleEnum.ROLE_SUPER_ADMIN.getValue())){
        	jwt = jwt+"_superKey"+tokenProvider.getSuperUserToken();
        }
        
        JWTToken jwtToken = new JWTToken();
        jwtToken.setIdToken(jwt);
        jwtToken.setUser(user);
        return jwtToken;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity update(String username,UserUpdateReq user) {
    
        // Calling DAO Function
        return userdao.updateUser(username,user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByName(String username) {
    
        // Calling DAO Function
        return userdao.getUserByName(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeUserPassword(String username, String password,String newPassword,
            String confPassword) {

        // Request validations
        if(newPassword.length()< 8){
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(PASSWORD_MIN_LEN));
        }
        
        if (!newPassword.equals(confPassword)) {
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(INVALID_PASSWORD));
        }
        
         // Calling DAO Function
        return userdao.changeUserPassword(username, password,newPassword);
                
        
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeUserPasswordRequest(String username) {
       
        // Calling DAO Function
        return userdao.changeUserPasswordRequest(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetUserPassword(String username, String token, String newPassword, String confPassword) {
        
     // Request validations
        if(newPassword.length()< 8){
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(PASSWORD_MIN_LEN));
        }
        
        if (!newPassword.equals(confPassword)) {
            throw new CustomValidationException(Status.VALIDATION_ERROR, Translator.toLocale(INVALID_PASSWORD));
        }
        
        // Calling DAO Function
        return userdao.resetUserPassword(username, token, newPassword);
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String verifyEmail(String username, String token) {
	       return userdao.verifyEmail(username, token);
		
	}

}

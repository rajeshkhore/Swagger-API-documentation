package net.bflows.pagafatture.service;

import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.model.UserUpdateReq;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.model.util.JWTToken;

public interface UserService {
    
    public UserEntity createUser(UserReq user);
    public JWTToken loginUser(String username, String password, Boolean rememberMe);
    public UserEntity update(String username,UserUpdateReq user);
    public UserEntity getUserByName(String username);
    public Boolean changeUserPassword(String username, String password,
            String newPassword, String confPassword);
    
    public Boolean resetUserPassword(String username,String token,
            String newPassword, String confPassword);
    
    public Boolean changeUserPasswordRequest(String username);
	
    public String verifyEmail(String username, String token);

}

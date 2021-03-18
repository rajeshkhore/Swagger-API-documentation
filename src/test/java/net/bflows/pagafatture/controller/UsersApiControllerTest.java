package net.bflows.pagafatture.controller;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.bflows.pagafatture.config.Translator;
import net.bflows.pagafatture.config.jwt.TokenProvider;
import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.model.UserReq;
import net.bflows.pagafatture.model.UserReq.UserStatusEnum;
import net.bflows.pagafatture.model.UserUpdateReq;
import net.bflows.pagafatture.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class UsersApiControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TokenProvider tokenProvider;

    @Test
    @Order(1)
    void testRegistration() throws Exception {

        UserReq user = new UserReq();
        
        user.setId(1l);
        user.setCompanyName("Test company");
        user.setConfPassword("test12345678");
        user.setEmail("testnew@gmail.com");
        user.setFirstName("test");
        user.setLastName("test lastname");
        user.setPassword("test12345678");
        user.setPhone("2424856");
        

        String URI = "/v1/users";
        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());/*.andExpect(jsonPath("$.data.merchant.name", is(user.getCompanyName())));*/
    }

    @Test
    @Order(2)
    void testLogin() throws Exception {
        
        String username="testnew@gmail.com";
        String password="test12345678";

        String URI = "/v1/users/" + username + "/sessions";

        mvc.perform(MockMvcRequestBuilders.post(URI).param("password", password)).andExpect(status().isOk());
        
        mvc.perform(MockMvcRequestBuilders.post(URI).param("password", password).param("rememberMe",String.valueOf(true))).andExpect(status().isOk());

    }
    
    @Test
    @Order(3)
    void testUpdateUser() throws Exception {

        String username="testnew@gmail.com";
        UserUpdateReq updateUserReq = new UserUpdateReq();
        updateUserReq.setFirstName("testedit");
        updateUserReq.setLastName("test lastname");
        updateUserReq.setPhone("242485685");

        String URI = "/v1/users/" + username;
        mvc.perform(
                MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateUserReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk()).andExpect(jsonPath("$.data.firstName", is(updateUserReq.getFirstName())));
        
        
        
        UserUpdateReq updateUserReq1 = new UserUpdateReq();
        updateUserReq1.setFirstName("testedit");
        updateUserReq1.setLastName("test lastname");
        updateUserReq1.setPhone("242485685");
        updateUserReq1.setRoleId(1l);

        mvc.perform(
                MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateUserReq1))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk()).andExpect(jsonPath("$.data.firstName", is(updateUserReq.getFirstName())));
        
    
        
        UserUpdateReq updateUserReq2 = new UserUpdateReq();
        updateUserReq2.setFirstName("testedit");
        updateUserReq2.setLastName("test lastname");
        updateUserReq2.setPhone("242485685");
        updateUserReq2.setRoleId(0l);

        mvc.perform(
                MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateUserReq2))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk()).andExpect(jsonPath("$.data.firstName", is(updateUserReq.getFirstName())));
    }
    
    @Test
    @Order(4)
    void testGetUserByuserName() throws Exception {
        
        String username="testnew@gmail.com";

        String URI = "/v1/users/" + username;

        mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isOk());

    }
    
    @Test
    @Order(5)
    void testGetUserByuserNameFail() throws Exception {
        
        String username="testnew2363478@gmail.com";

        String URI = "/v1/users/" + username;

        mvc.perform(MockMvcRequestBuilders.get(URI)).andExpect(status().isNotFound());

    }
    
    @Test
    @Order(6)
    void testLogout() throws Exception {
        
        String username="testnew@gmail.com";

        String URI = "/v1/users/" + username + "/sessions";

        mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isOk());

    }
    
    @Test
    @Order(7)
    void testChangeUserPasswordRequest() throws Exception {
        
        String username="testnew@gmail.com";

        String URI = "/v1/users/" + username + "/passwords";

        mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isOk());

    }
    
    @Test
    @Order(8)
    void testResetUserPassword_Fail() throws Exception {
        
    	  String username="testnew@gmail.com";
          UserEntity user= userRepository.findByEmailIgnoreCase(username).get();
         
          String token=user.getTempToken().split(":")[0];
          String newPassword="test12345678";
          String confPassword="test12345678";

          String URI = "/v1/users/" + "xyz@gmail.com" + "/passwords?token="+token+"&newPassword="
                  +newPassword+"&confPassword="+confPassword;

          mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isNotFound());

    }
    
    
    @Test
    @Order(9)
    void testResetUserPassword() throws Exception {
        
        String username="testnew@gmail.com";
        UserEntity user= userRepository.findByEmailIgnoreCase(username).get();
       
        String token=user.getTempToken().split(":")[0];
        String newPassword="test12345678";
        String confPassword="test12345678";


        String URI = "/v1/users/" + username + "/passwords?token="+token+"&newPassword="
                +"test"+"&confPassword="+"test";

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isBadRequest());
        
        URI = "/v1/users/" + username + "/passwords?token="+token+"&newPassword="
                +"test@12345"+"&confPassword="+"test@123";

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isBadRequest());
        
        URI = "/v1/users/" + username + "/passwords?token="+"dfhfbfvnvhf:dfkf"+"&newPassword="
                +newPassword+"&confPassword="+confPassword;

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isBadRequest());
        
        URI = "/v1/users/" + username + "/passwords?token="+token+"&newPassword="
                +newPassword+"&confPassword="+confPassword;

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isOk());

    }
    
    
    @Test
    @Order(10)
    void testChangeUserPassword() throws Exception {
        
        String username="testnew@gmail.com";
        String oldPassword="test12345678";
        String newPassword="test12345678";
        String confPassword="test12345678";

        String URI = "/v1/users/" + username + "/passwords-auth?password="+oldPassword+"&newPassword="
                +newPassword+"&confPassword="+confPassword;

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isOk());

    }

    @Test
    @Order(11)
    void testGetSuperUserFromSuperUserToken() throws Exception {
    	org.springframework.security.core.userdetails.User principal=tokenProvider.getSuperAdminUser("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QudGVzdCIsImF1dGgiOiJST0xFX0FETUlOIiwiZXhwIjoxNjAzOTgzNzI0fQ.MiJsH782PjBjkYQXGrdr42MIlcu5K61xnxptebtcXnPAPSGVIZwub-v4_uiGH6D_6ZwPTdKNxLUhy7RJtUpAUQ_superKey$2a$10$ZTBwN.ZHXsr/iysflbS6ZecxKhFCg3cEEDGMrjqLk2lw3dZ9wSl4C");
        assertNotNull(principal);
    }

    
    @Test
    @Order(12)
    void testRegistration_FailByRole() throws Exception {

        UserReq user = new UserReq();
        
        user.setId(1l);
        user.setCompanyName("Test company");
        user.setConfPassword("test12345678");
        user.setEmail("testnew1@gmail.com");
        user.setFirstName("test");
        user.setLastName("test lastname");
        user.setPassword("test12345678");
        user.setPhone("2424856");
        user.setRoleId(55L);
        

        String URI = "/v1/users";
        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isNotFound());/*.andExpect(jsonPath("$.data.merchant.name", is(user.getCompanyName())));*/
    }
    
    @Test
    @Order(13)
    void testUpdateUser_Fail() throws Exception {

        String username="xyz@gmail.com";
        UserUpdateReq updateUserReq = new UserUpdateReq();
        updateUserReq.setFirstName("testedit");
        updateUserReq.setLastName("test lastname");
        updateUserReq.setPhone("242485685");

        String URI = "/v1/users/" + username;
        mvc.perform(
                MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateUserReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isNotFound());
    }
    
    
    @Test
    @Order(14)
    void testChangeUserPassword_Fail() throws Exception {
        
        String username="xyz@gmail.com";
        String oldPassword="test12345678";
        String newPassword="test12345678";
        String confPassword="test12345678";

        String URI = "/v1/users/" + username + "/passwords-auth?password="+oldPassword+"&newPassword="
                +newPassword+"&confPassword="+confPassword;

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isNotFound());

    }
    
    @Test
    @Order(15)
    void testChangeUserPasswordRequest_Fail() throws Exception {
        
        String username="xyz@gmail.com";

        String URI = "/v1/users/" + username + "/passwords";

        mvc.perform(MockMvcRequestBuilders.delete(URI)).andExpect(status().isNotFound());

    }
    
 
    @Test
    @Order(16)
    void testUpdateUser_FailInvalidRole() throws Exception {

        String username="testnew@gmail.com";
        UserUpdateReq updateUserReq = new UserUpdateReq();
        updateUserReq.setFirstName("testedit");
        updateUserReq.setLastName("test lastname");
        updateUserReq.setPhone("242485685");
        updateUserReq.setRoleId(101l);
        String URI = "/v1/users/" + username;
        mvc.perform(
                MockMvcRequestBuilders.put(URI).content(mapper.writeValueAsString(updateUserReq))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isNotFound());
    }
    
    
    @Test
    @Order(17)
    void testRegistrationWithRole() throws Exception {

        UserReq user = new UserReq();
        
        user.setId(1l);
        user.setCompanyName("Test company");
        user.setConfPassword("test12345678");
        user.setEmail("testrNew@gmail.com");
        user.setFirstName("test");
        user.setLastName("test lastname");
        user.setPassword("test12345678");
        user.setPhone("2424856");
        user.setRoleId(1l);
        

        String URI = "/v1/users";
        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
        
        
        
        UserReq user1 = new UserReq();
        
        user1.setId(1l);
        user1.setCompanyName("Test company 2");
        user1.setConfPassword("test12345678");
        user1.setEmail("testfhjdfNew@gmail.com");
        user1.setFirstName("test");
        user1.setLastName("test lastname");
        user1.setPassword("test12345678");
        user1.setPhone("2424856");
        

        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
        
        
        
         UserReq user2 = new UserReq();
        
         user2.setId(1l);
         user2.setCompanyName("Test company 2");
         user2.setConfPassword("test12345678");
         user2.setEmail("testfhrdjdfNew@gmail.com");
         user2.setFirstName("test");
         user2.setLastName("test lastname");
         user2.setPassword("test12345678");
         user2.setPhone("2424856");
         user2.setRoleId(0l);
        

        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
        
        

       
    }
    
    
    @Test
    @Order(18)
    void testRegistrationFaildByPassword() throws Exception {

        UserReq user = new UserReq();
        
        user.setId(1l);
        user.setCompanyName("Test company");
        user.setConfPassword("test12345678");
        user.setEmail("testnew@gmail.com");
        user.setFirstName("test");
        user.setLastName("test lastname");
        user.setPassword("test12345678");
        user.setPhone("2424856");
        

        String URI = "/v1/users";
        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isBadRequest());/*.andExpect(jsonPath("$.data.merchant.name", is(user.getCompanyName())));*/
        
        
        UserReq user1 = new UserReq();
        
        user1.setId(1l);
        user1.setCompanyName("Test company");
        user1.setConfPassword("test");
        user1.setEmail("testfhvnknew@gmail.com");
        user1.setFirstName("test");
        user1.setLastName("test lastname");
        user1.setPassword("test");
        user1.setPhone("2424856");
        

        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isBadRequest());/*.andExpect(jsonPath("$.data.merchant.name", is(user.getCompanyName())));*/
        
        
        UserReq user3 = new UserReq();
        
        user3.setId(1l);
        user3.setCompanyName("Test company");
        user3.setConfPassword("test@123");
        user3.setEmail("dfff@gmail.com");
        user3.setFirstName("test");
        user3.setLastName("test lastname");
        user3.setPassword("test@12345");
        user3.setPhone("2424856");
        

        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user3))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isBadRequest());/*.andExpect(jsonPath("$.data.merchant.name", is(user.getCompanyName())));*/
    }
    
    
    @Test
    @Order(19)
    void testLoginForSuperUser() throws Exception {
        
       
    	  UserReq user = new UserReq();
          
          user.setId(1l);
          user.setCompanyName("Test company");
          user.setConfPassword("test12345678");
          user.setEmail("testSperAdmin@gmail.com");
          user.setFirstName("test");
          user.setLastName("test lastname");
          user.setPassword("test12345678");
          user.setPhone("2424856");
          user.setRoleId(4l);
          

          String URI = "/v1/users";
          mvc.perform(
                  MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
                          .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                  status().isCreated());/*.andExpect(jsonPath("$.data.merchant.name", is(user.getCompanyName())));*/
    	

          

          URI = "/v1/users/" + user.getEmail() + "/sessions";

          mvc.perform(MockMvcRequestBuilders.post(URI).param("password", user.getPassword())).andExpect(status().isOk());
    }
    
    
    @Test
    @Order(20)
    void testChangeUserPassword_Faild() throws Exception {
        
        String username="testnew@gmail.com";
        String oldPassword="test12345678";
        String newPassword="test";
        String confPassword="test";

        String URI = "/v1/users/" + username + "/passwords-auth?password="+oldPassword+"&newPassword="
                +newPassword+"&confPassword="+confPassword;

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isBadRequest());
        
        oldPassword="test12345678";
        newPassword="test@12345";
        confPassword="test@123";

        URI = "/v1/users/" + username + "/passwords-auth?password="+oldPassword+"&newPassword="
                +newPassword+"&confPassword="+confPassword;

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isBadRequest());
        
        
        
        oldPassword="test15278";
        newPassword="test@12345";
        confPassword="test@12345";

        URI = "/v1/users/" + username + "/passwords-auth?password="+oldPassword+"&newPassword="
                +newPassword+"&confPassword="+confPassword;

        mvc.perform(MockMvcRequestBuilders.put(URI)).andExpect(status().isBadRequest());

    }
    
    @Test
    @Order(21)
    void testLoginFail() throws Exception {
        
        String username="testnew@gmail.cominvalid";
        String password="test12345678";

        String URI = "/v1/users/" + username + "/sessions";

        mvc.perform(MockMvcRequestBuilders.post(URI).param("password", password)).andExpect(status().isUnauthorized());

    }
    
    @Test
    @Order(22)
    void testRegistrationFail() throws Exception {

        UserReq user = new UserReq();
        
        user.setId(1l);
        user.setCompanyName("Test company");
        user.setConfPassword("test12345678");
        user.setEmail("testnew@gmail.com");
        user.setLastName("test lastname");
        user.setPassword("test12345678");
        user.setPhone("2424856");
        

        String URI = "/v1/users";
        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isBadRequest());/*.andExpect(jsonPath("$.data.merchant.name", is(user.getCompanyName())));*/
    }
    
    @Test
    @Order(23)
    void testRegistrationWithEmailConfirmation() throws Exception {

        UserReq user = new UserReq();
        
        user.setId(1l);
        user.setCompanyName("Test company");
        user.setConfPassword("test12345678");
        user.setEmail("testEmail@gmail.com");
        user.setFirstName("test");
        user.setLastName("test lastname");
        user.setPassword("test12345678");
        user.setPhone("2424856");
        user.setRoleId(1l);
        

        String URI = "/v1/users";
        mvc.perform(
                MockMvcRequestBuilders.post(URI).content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
        
        
        Optional<UserEntity> userEnt = userRepository.findByUserStatusAndEmailIgnoreCase(
                UserStatusEnum.ACTIVE.getValue(), user.getEmail());
        
        UserEntity userEntity = userEnt.get();
        
        String dbToken = userEnt.get().getEmailToken();
        String[] dbTokenArray = dbToken.split(":");

        String generatedToken = dbTokenArray[0];
       
        URI =  "/v1/users/emails/"+user.getEmail()+1+"?token="+generatedToken;
        mvc.perform(
                MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isNotFound());
        
        
        URI = "/v1/users/emails/"+user.getEmail()+"?token="+2121439;
        mvc.perform(
                MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isBadRequest());
        
        URI = "/v1/users/emails/"+user.getEmail()+"?token="+generatedToken;
        mvc.perform(
                MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
        
        mvc.perform(
                MockMvcRequestBuilders.post(URI)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk()).andExpect(jsonPath("$.message", is(Translator.toLocale("email_verified"))));
    }

}
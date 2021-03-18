package net.bflows.pagafatture.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.bflows.pagafatture.entities.UserEntity;
import net.bflows.pagafatture.model.UserReq.UserStatusEnum;
import net.bflows.pagafatture.repositories.UserRepository;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {

        log.debug("Authenticating {}", email);

        Optional<UserEntity> userByLoginFromDatabase = userRepository.findByUserStatusAndEmailIgnoreCase(
                UserStatusEnum.ACTIVE.getValue(), email);
        return userByLoginFromDatabase.map(user -> createSpringSecurityUser(user)).orElseThrow(
                () -> new UsernameNotFoundException("User " + email + " was not found in the database"));

    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(UserEntity user) {

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole().getRoleName());

        List<GrantedAuthority> grantedAuthorities = new LinkedList<GrantedAuthority>();
        grantedAuthorities.add(simpleGrantedAuthority);

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                grantedAuthorities);
    }
}

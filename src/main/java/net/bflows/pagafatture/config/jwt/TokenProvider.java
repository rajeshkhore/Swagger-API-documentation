package net.bflows.pagafatture.config.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import net.bflows.pagafatture.repositories.UserRepository;
import net.bflows.pagafatture.util.MailUtil;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final Base64.Encoder encoder = Base64.getEncoder();

    @Value("${security.authentication.jwt.secret}")
    private String secretKey;

    @Value("${security.authentication.jwt.token-validity-in-seconds}")
    private Long tokenValidityInSeconds;

    @Autowired
    UserRepository userRepository;
    
    @Value("${super_user_token}")
    public String superUserToken;
    
    @Autowired
    private MailUtil mailUtil;
    
    @PostConstruct
    public void init() {
        this.secretKey = encoder.encodeToString(this.secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication,Long merchantId, Boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Long now = null;
        Date validity=null;
        if(Boolean.TRUE.equals(rememberMe)) {
        	now=DateUtils.addMonths(new Date(), 2).getTime();
        	validity = new Date(now);
       }else {
    	   now=(new Date()).getTime();
    	   validity = new Date(now + (this.tokenValidityInSeconds * 1000));
       }
        String payload = authentication.getName();
        if(merchantId != null) {
    	   payload = authentication.getName()+","+merchantId;  
       }
       
        return Jwts.builder().setSubject(payload).claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, secretKey).setExpiration(validity).compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        String[] payload= claims.getSubject().split(",");
             

        User principal = new User(payload[0], "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    public String getUserDetails(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public boolean validateToken(String authToken) {

        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

	public String getSuperUserToken() {
		return superUserToken;
	}
	
	public User getSuperAdminUser(String token) {

		Map<String, Object> payload = getUserDetailsForSuperUser(token);
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(payload.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		String[] payloadString = payload.get("sub").toString().split(",");
		return new User(payloadString[0], "", authorities);
	}

	public Map<String, Object> getUserInfoFromToken(HttpServletRequest request) {
		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("merchantId", null);
		 if (Boolean.FALSE.equals(mailUtil.isTestProfileActivated())) {
			
			String bearerToken = request.getHeader("Authorization");
			String token = "";
			if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
				token = bearerToken.substring(7, bearerToken.length());
			}
			String payload = null;
			if (StringUtils.hasText(token) && token.endsWith("_superKey" + getSuperUserToken())) {
				Map<String, Object> payloadMap = getUserDetailsForSuperUser(token);
				payload=payloadMap.get("sub").toString();
				
			} else {
				payload = getUserDetails(token);
			}
			String[] payloadString = payload.split(",");
			if(payloadString.length>1) {
				userInfo.put("merchantId", payloadString[1]);
			}
		}
		

		return userInfo;
	}

	public Map<String, Object> getUserDetailsForSuperUser(String token) {
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String[] parts = token.split("\\."); // Splitting header, payload and signature
		Gson g = new Gson();
		return g.fromJson(new String(decoder.decode(parts[1])), Map.class);
	}
}

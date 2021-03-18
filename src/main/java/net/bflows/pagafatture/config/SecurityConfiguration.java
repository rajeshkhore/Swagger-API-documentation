package net.bflows.pagafatture.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import javax.annotation.PostConstruct;

import net.bflows.pagafatture.config.jwt.JWTConfigurer;
import net.bflows.pagafatture.config.jwt.TokenProvider;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserDetailsService userDetailsService;

    private final TokenProvider tokenProvider;

    private final CorsFilter corsFilter;

    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder,
            UserDetailsService userDetailsService, TokenProvider tokenProvider, CorsFilter corsFilter,
            SecurityProblemSupport problemSupport) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
    }

    @PostConstruct
    public void init() {
        try {
            authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/i18n/**").antMatchers("/content/**").antMatchers("/swagger-ui/index.html");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class).exceptionHandling()
                .authenticationEntryPoint(problemSupport).accessDeniedHandler(problemSupport).and().csrf().disable()
                .headers().frameOptions().disable().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers(HttpMethod.POST,"/v1/users/{username}/sessions").permitAll()
                .antMatchers(HttpMethod.DELETE,"/v1/users/{username}/passwords").permitAll()
                .antMatchers(HttpMethod.PUT,"/v1/users/{username}/passwords").permitAll()
                .antMatchers("/v1/api-docs").permitAll()
                .antMatchers("/v1/clients/invoices").permitAll()
                .antMatchers(HttpMethod.POST,"/v1/users/emails/{username}").permitAll()
                .antMatchers(HttpMethod.POST,"/v1/users").permitAll().antMatchers("/v1/**").authenticated()
                .antMatchers("/management/**").permitAll().antMatchers("/swagger-resources/configuration/ui")
                .permitAll().antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN).and()
                .apply(securityConfigurerAdapter());

    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .addServersItem(new Server().url("/"))
            .addSecurityItem(new SecurityRequirement().addList("bearer-key")).components(
                new Components().addSecuritySchemes("bearer-key", new SecurityScheme().type(SecurityScheme.Type.HTTP)
                        .scheme("bearer").bearerFormat("JWT")));
    }

}

package com.gkiss01.meetdebwebapi;

import com.gkiss01.meetdebwebapi.exception.AccessIsDeniedHandler;
import com.gkiss01.meetdebwebapi.exception.UnAuthorizedHandler;
import com.gkiss01.meetdebwebapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class MeetdebWebApiSecurity extends WebSecurityConfigurerAdapter {

    private static final String API_URL_USER = "/users";
    private static final String API_URL_EVENT = "/events";
    private static final String API_URL_IMAGE = "/images/**";

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UnAuthorizedHandler unAuthorizedHandler;
    private final AccessIsDeniedHandler accessIsDeniedHandler;

    @Autowired
    public MeetdebWebApiSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, UnAuthorizedHandler unAuthorizedHandler, AccessIsDeniedHandler accessIsDeniedHandler) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.unAuthorizedHandler = unAuthorizedHandler;
        this.accessIsDeniedHandler = accessIsDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers(HttpMethod.POST, API_URL_USER).permitAll()
                .antMatchers(HttpMethod.GET, API_URL_USER + "/confirm").permitAll()
                .antMatchers(HttpMethod.POST, API_URL_EVENT).permitAll()
                .antMatchers(HttpMethod.GET, API_URL_IMAGE).permitAll()
                .anyRequest().authenticated()
                .and().headers().frameOptions().sameOrigin()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().httpBasic()
                .and().exceptionHandling().authenticationEntryPoint(unAuthorizedHandler).accessDeniedHandler(accessIsDeniedHandler);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}

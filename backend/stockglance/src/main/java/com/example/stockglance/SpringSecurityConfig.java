package com.example.stockglance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
//Marks this class as a configuration class. This tells Spring that this class contains configurations that will be used to define beans or settings for your application.
@EnableWebSecurity
//This enables Spring Security's web security support. By adding this annotation, Spring Security is activated for your web application, which applies security settings like authentication, authorization, etc.
public class SpringSecurityConfig  {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//        CSRF (Cross-Site Request Forgery) protection is disabled here.
//        What is CSRF? CSRF is a type of attack where unauthorized commands are transmitted from a user that a web application trusts. Spring Security enables CSRF protection by default to prevent this kind of attack.
//                Why disable CSRF?
//        For stateless APIs (such as REST APIs using JWT tokens), you often disable CSRF because there are no sessions or cookies involved in tracking the user state. Since the API is stateless, CSRF is unnecessary.
//        If you're building a traditional web application that uses sessions or forms, CSRF protection is typically left enabled to safeguard against such attacks.
//        By disabling CSRF with csrf.disable(), you're telling Spring Security not to require CSRF tokens for POST, PUT, DELETE, or other non-GET requests. This is commonly done in API-based applications.
        http
                .csrf((csrf) -> csrf.disable());

        // After setting the configurations (in this case, only disabling CSRF), the build() method finalizes and returns the SecurityFilterChain object, which defines the security filters applied to incoming requests
        return http.build();
    }
}




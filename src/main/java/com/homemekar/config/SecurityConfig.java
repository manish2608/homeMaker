package com.homemekar.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	public AuthenticationSuccessHandler customSuccessHandler;
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new  BCryptPasswordEncoder() ;
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
		
		
		return daoAuthenticationProvider;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http.authenticationProvider(authenticationProvider());
		http
		.csrf().disable()
		.authorizeRequests() 
		.requestMatchers("/admin/**").hasRole("ADMIN")
		.requestMatchers("/user/**").hasRole("APNAGHAR")
				.requestMatchers("/contractor/").hasRole("CONTRACTOR")
				.requestMatchers("/**")
				.permitAll().and().formLogin().loginPage("/signin").loginProcessingUrl("/login")
				.successHandler(customSuccessHandler)
				.and().logout().deleteCookies("dummyCookie").logoutSuccessUrl("/signin")
				.and()
				.sessionManagement().invalidSessionUrl("/signin?invalid-session=true");

		return http.build();
	}
	
	@Bean
	public SessionRegistry sessionRegistry() {
		SessionRegistry sessionRegistry = new SessionRegistryImpl();
		return sessionRegistry;
	}
	
	@Bean
	public HttpSessionEventPublisher httpSessionEventPulisher() {
		return new HttpSessionEventPublisher();
	}
}

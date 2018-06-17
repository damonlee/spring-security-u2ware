package io.github.u2ware.apps.login;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.rememberme.PersistentHeaderBasedRememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true, jsr250Enabled=true)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

	protected final Log logger = LogFactory.getLog(getClass());

	private String rememberMeKey = "Custom RememberMe Authentication Provider Key";
	
	private @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	private @Autowired UserAccountService userAccountService;
	private @Autowired UserTokenService userTokenService;
	private @Autowired UserEventService userEventService;
	private @Autowired UserPasswordEncoder userPasswordEncoder;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userAccountService).passwordEncoder(userPasswordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http
		    	.csrf()
		    		.disable()
			.formLogin()
				.successHandler(userEventService)
				.failureHandler(userEventService)
				.permitAll()
				.and()
			.logout()
				.logoutSuccessHandler(userEventService)
				.deleteCookies("JSESSIONID")
				.permitAll()
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(userEventService)
				.accessDeniedHandler(userEventService)
				.and()
			.authorizeRequests()
				.antMatchers(springDataRestBasePath+"/**").authenticated()
				.anyRequest().permitAll()
				.and()
				
			//////////////////////////////////////////////////
			// For 3rd App..
			//////////////////////////////////////////////////
			.cors()
				.and()
			.rememberMe()
				.rememberMeServices(
						new PersistentHeaderBasedRememberMeServices(
								rememberMeKey, 
								userAccountService, 
								userTokenService,
								userEventService))
				.key(rememberMeKey)
				.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			;
	}

	
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration configuration = new CorsConfiguration();
		configuration.setMaxAge(3600L);
        configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        //서버측에서 요청 헤더를 읽을 수 있도록 설정
		configuration.setAllowedHeaders(Arrays.asList("*"));
        //클라이언트측에서 응답 헤더를  읽을 수 있도록 설정
		configuration.setExposedHeaders(Arrays.asList("Authorization", "xsrf-token"));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

//CorsFilter f1;
//CsrfFilter f2;
//HttpSessionCsrfTokenRepository  f;

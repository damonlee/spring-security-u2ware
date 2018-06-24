package io.github.u2ware.apps;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationHandler;
import org.springframework.security.web.authentication.UserDetailsServiceDelegate;
import org.springframework.security.web.authentication.rememberme.PersistentHeaderTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true, jsr250Enabled=true)
@SuppressWarnings("deprecation")
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

	protected final Log logger = LogFactory.getLog(getClass());

	private String rememberMeKey = "Custom RememberMe Authentication Provider Key";

	private @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	private @Autowired UserDetailsService[] userDetailsServices;
	private @Autowired AuthenticationHandler authenticationHandler;
	private @Autowired PersistentTokenRepository persistentTokenRepository;
	private @Autowired UserPasswordEncoder passwordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(new UserDetailsServiceDelegate(userDetailsServices)).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
				.disable()
			.formLogin()
				.successHandler(authenticationHandler)
				.failureHandler(authenticationHandler)
				.permitAll()
				.and()
			.logout()
				.logoutSuccessHandler(authenticationHandler)
				.deleteCookies("JSESSIONID")
				.permitAll()
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(authenticationHandler)
				.accessDeniedHandler(authenticationHandler)
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
						new PersistentHeaderTokenBasedRememberMeServices(
								rememberMeKey, 
								new UserDetailsServiceDelegate(userDetailsServices), 
								persistentTokenRepository,
								authenticationHandler))
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
		// 서버측에서 요청 헤더를 읽을 수 있도록 설정
		configuration.setAllowedHeaders(Arrays.asList("*"));
		// 클라이언트측에서 응답 헤더를 읽을 수 있도록 설정
		configuration.setExposedHeaders(Arrays.asList("Authorization", "xsrf-token"));
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Component
	public static class UserPasswordEncoder extends ShaPasswordEncoder {

		public UserPasswordEncoder() {
			super(256);
		}

		public String encode(String rawPass) {
			return super.encodePassword(rawPass, null);
		}
	}

}

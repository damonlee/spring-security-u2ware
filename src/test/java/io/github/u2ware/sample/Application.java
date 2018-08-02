package io.github.u2ware.sample;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@EnableJpaAuditing
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@ControllerAdvice
	public static class BaseControllerAdvice {

		protected Log logger = LogFactory.getLog(getClass());

		@ExceptionHandler(Throwable.class)
		public @ResponseBody ResponseEntity<Object> handleException(final Exception exception,
				final HttpServletRequest request) {

			String message = request.getRequestURL() 
							+ " [" + exception.getClass().getName() 
							+ ": "
							+ exception.getMessage() + "] " 
							+ System.currentTimeMillis();
			logger.warn(message, exception);
			logger.warn(ClassUtils.isAssignableValue(AccessDeniedException.class, exception));

			HttpStatus status =
					ClassUtils.isAssignableValue(AccessDeniedException.class, exception)
					|| ClassUtils.isAssignableValue(AuthenticationException.class, exception) 
					|| ClassUtils.isAssignableValue(InsufficientAuthenticationException.class, exception)
					? HttpStatus.UNAUTHORIZED
					: HttpStatus.INTERNAL_SERVER_ERROR;
			return new ResponseEntity<Object>(message, status);
		}
	}
}
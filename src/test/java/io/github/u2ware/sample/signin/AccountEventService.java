package io.github.u2ware.sample.signin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AccountEventService implements AuthenticationHandler {

	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired ObjectMapper objectMapper;
	private @Autowired AccountEventRepository accountEventRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		logger.info(request.getRequestURL() + " [onAuthenticationSuccess]: " + authentication);
		processAuthenticationSuccess(request, response, authentication);
		Object message = retrieveUserDetails(authentication);
		sendResponse(request, response, HttpServletResponse.SC_OK, message);
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		logger.info(request.getRequestURL() + " [onAuthenticationFailure]: " + exception);
		processAuthenticationFailure(request, response, exception);
		sendResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED, exception);
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		logger.info(request.getRequestURL() + "[onLogoutSuccess]: " + authentication);
		if (authentication != null) {
			processLogoutSuccess(request, response, authentication);
		}
		Object message = retrieveUserDetails(authentication);
		sendResponse(request, response, HttpServletResponse.SC_OK, message);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		logger.info(request.getRequestURL() + " [handle]: " + accessDeniedException);
		processExceptionCaught(request, response, accessDeniedException);
		sendResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED, accessDeniedException.getMessage());
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.info(request.getRequestURL() + "[commence]: " + authException);
		processExceptionCaught(request, response, authException);
		sendResponse(request, response, HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
	}

	public void processAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		logger.info(request.getRequestURL() + " [processAuthenticationSuccess]: " + authentication);

		AccountEvent e = new AccountEvent();
		e.setUsername(authentication.getName());
		e.setEventType("login");
		e.setEventDate(System.currentTimeMillis());
		accountEventRepository.save(e);
	}

	public void processAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		logger.info(request.getRequestURL() + " [processAuthenticationFailure]: " + exception);
	}

	public void processLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		logger.info(request.getRequestURL() + " [processLogoutSuccess]: " + authentication);

		AccountEvent e = new AccountEvent();
		e.setUsername(authentication.getName());
		e.setEventType("logout");
		e.setEventDate(System.currentTimeMillis());
		accountEventRepository.save(e);
	}

	public void processExceptionCaught(HttpServletRequest request, HttpServletResponse response, Exception exception)
			throws IOException, ServletException {
		logger.info(request.getRequestURL() + " [processExceptionCaught]: " + exception);
	}

	protected Object retrieveUserDetails(Authentication authentication) {
		if (authentication == null)
			return "Unknown";

		if (authentication.getPrincipal() instanceof UserDetails) {
			return authentication.getPrincipal();
		} else {
			return authentication.getPrincipal().toString();
		}
	}

	protected void sendResponse(HttpServletRequest request, HttpServletResponse response, int status,
			Throwable exception) throws IOException {
		String message = request.getRequestURL() + " [" + exception.getClass().getName() + ": " + exception.getMessage()
				+ "] " + System.currentTimeMillis();
		logger.debug(message, exception);
		response.sendError(status, message);
	}

	protected void sendResponse(HttpServletRequest request, HttpServletResponse response, int status, Object object)
			throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(status);
		PrintWriter writer = response.getWriter();
		writer.write(objectMapper.writeValueAsString(object));
		writer.flush();
		writer.close();
	}
}
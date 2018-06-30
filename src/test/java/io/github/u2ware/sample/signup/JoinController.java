package io.github.u2ware.sample.signup;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JoinController {

	protected final Log logger = LogFactory.getLog(getClass());

	private @Autowired JoinService userService;

	// 아이디 사용가능 여부
	@RequestMapping(value = "/join", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> username(
			@RequestParam(value = "username") String username)throws Exception {

		boolean exist = userService.userExists(username);
		if (exist)
			throw new Exception("Username Already Exists");
		return new ResponseEntity<Object>(exist, HttpStatus.OK);
	}

	// 회원가입
	@RequestMapping(value = "/join", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Object> register(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password) throws Exception {
		boolean exist = userService.userExists(username);
		if (exist)
			throw new Exception("Username Already Exists");
		Object user = userService.createUser(username, password);
		return new ResponseEntity<Object>(user, HttpStatus.OK);
	}

	// 회원가입 verification
	@RequestMapping(value = "/join/{verification}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> verify(
			@PathVariable("verification") UUID verification) throws Exception {

		boolean exist = userService.userVerify(verification);
		if (exist)
			throw new Exception("Verification Failure");
		return new ResponseEntity<Object>(exist, HttpStatus.OK);
	}
}
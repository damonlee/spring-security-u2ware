package io.github.u2ware.sample.account;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@BasePathAwareController
public class AccountController {

	protected final Log logger = LogFactory.getLog(getClass());

	private @Autowired AccountService accountRestService;

	// 로그온 사용자 정보
	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Object> account() throws Exception {
		Object source = accountRestService.readUser();
		return new ResponseEntity<Object>(source, HttpStatus.OK);
	}

	// 로그인 사용자 이름 변경
	@RequestMapping(value = "/account", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Object> account(
			@RequestParam("nickname") String nickname) throws Exception {

		Object source = accountRestService.changeNickname(nickname);
		return new ResponseEntity<Object>(source, HttpStatus.OK);
	}

	// 로그온 사용자 패스워드 변경
	@RequestMapping(value = "/account", method = RequestMethod.PATCH)
	public @ResponseBody ResponseEntity<Object> account(
			@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword) throws Exception {

		Object source = accountRestService.changePassword(oldPassword, newPassword);
		return new ResponseEntity<Object>(source, HttpStatus.OK);
	}

	// 로그온 사용자 정보 수정
	@RequestMapping(value = "/account", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Object> account(
			@RequestParam MultiValueMap<String, Object> params) throws Exception {

		Object source = accountRestService.updateUser(params);
		return new ResponseEntity<Object>(source, HttpStatus.OK);
	}

	// 로그온 사용자 회원탈퇴
	@RequestMapping(value = "/account", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<Object> unregister(
			@RequestParam MultiValueMap<String, Object> params) throws Exception {

		Object source = accountRestService.deleteUser(params);
		return new ResponseEntity<Object>(source, HttpStatus.OK);
	}
}
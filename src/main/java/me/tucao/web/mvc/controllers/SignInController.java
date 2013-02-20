package me.tucao.web.mvc.controllers;

import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import me.tucao.constants.AjaxResultCode;
import me.tucao.constants.ApplicationConstants;
import me.tucao.constants.UserStatus;
import me.tucao.domains.User;
import me.tucao.domains.UserPreference;
import me.tucao.exceptions.ResourceNotFoundException;
import me.tucao.repositories.UserPreferenceRepository;
import me.tucao.repositories.UserRepository;
import me.tucao.vo.AjaxResult;
import me.tucao.vo.SignInCredentialVo;
import me.tucao.vo.ValidationEngineError;
import me.tucao.web.utils.AjaxUtil;
import me.tucao.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SignInController {

	@Autowired
	UserRepository userPepository;
	@Autowired
	UserPreferenceRepository userPreferenceRepository;
	@Autowired
	SessionUtil sessionUtil;
	@Autowired
	AjaxUtil ajaxUtil;

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public String signIn(HttpSession session) {
		if (session.getAttribute(ApplicationConstants.SESSION_SIGNIN_USER) != null) {
			return "redirect:/";
		}
		return "sign.in";
	}

	@RequestMapping(value = "/checksignin", method = RequestMethod.GET)
	public @ResponseBody
	AjaxResult checkSignIn(HttpServletRequest request, ModelAndView mav,
			HttpSession session) {
		if (!ajaxUtil.isAjaxRequest(request)) {
			throw new ResourceNotFoundException();
		}
		if (sessionUtil.getSignInUser(session) != null) {
			Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("UTC"));
			return new AjaxResult(AjaxResultCode.SUCCESS, c.getTimeInMillis());
		} else {
			return new AjaxResult(AjaxResultCode.NEED_SIGNIN);
		}
	}

	@RequestMapping(value = "/signin/validate", method = RequestMethod.POST)
	public @ResponseBody
	Object[] validateSignIn(@Valid SignInCredentialVo signInCredentialVo,
			BindingResult result, Model model, HttpSession session) {
		User existed = null;
		if (!result.hasFieldErrors("signInName")) {
			existed = userPepository.getByEmail(signInCredentialVo
					.getSignInName());
			if (existed == null) {
				result.addError(new FieldError("signInCredentialVo",
						"signInName", "注册邮箱不存在"));
			} else {
				if (!signInCredentialVo.getSignInPassword().equals(
						existed.getPassword())) {
					result.addError(new FieldError("signInCredentialVo",
							"signInPassword", "密码不正确"));
				}
				if(existed.getStatus() == UserStatus.INVALID){
					result.addError(new FieldError("signInCredentialVo",
							"signInName", "账号暂时不能登陆"));
				}
			}
		}
		if (result.hasErrors()) {
			return ValidationEngineError.normalize(ValidationEngineError
					.from(result));
		}else{
			if (existed != null) {
				session.setAttribute(ApplicationConstants.SESSION_SIGNIN_USER,
						existed);
				UserPreference up = userPreferenceRepository.getByUser(existed);
				if (up != null) {
					sessionUtil.setSignInUserPrefer(up, session);
				}
			}
			return new ValidationEngineError[]{};
		}
	}

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	public String signIn(@Valid SignInCredentialVo signInCredentialVo,
			BindingResult result, Model model, HttpSession session) {
		User existed = null;
		if (!result.hasFieldErrors("signInName")) {
			existed = userPepository.getByEmail(signInCredentialVo
					.getSignInName());
			if (existed == null) {
				result.addError(new FieldError("signInCredentialVo",
						"signInName", "注册邮箱不存在"));
			} else {
				if (!signInCredentialVo.getSignInPassword().equals(
						existed.getPassword())) {
					result.addError(new FieldError("signInCredentialVo",
							"signInPassword", "密码不正确"));
				}
				if(existed.getStatus() == UserStatus.INVALID){
					result.addError(new FieldError("signInCredentialVo",
							"signInName", "账号暂时不能登陆"));
				}
			}
		}

		if (result.hasErrors()) {
			return "sign.in";
		}

		if (existed != null) {
			session.setAttribute(ApplicationConstants.SESSION_SIGNIN_USER,
					existed);
			UserPreference up = userPreferenceRepository.getByUser(existed);
			if (up != null) {
				sessionUtil.setSignInUserPrefer(up, session);
			}
		}

		return "redirect:/";
	}
}

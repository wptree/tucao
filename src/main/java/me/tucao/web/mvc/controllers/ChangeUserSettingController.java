package me.tucao.web.mvc.controllers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import me.tucao.constants.AjaxResultCode;
import me.tucao.constants.ApplicationConfig;
import me.tucao.constants.ApplicationConstants;
import me.tucao.constants.Gender;
import me.tucao.domains.Resource;
import me.tucao.domains.User;
import me.tucao.repositories.ResourceRepository;
import me.tucao.repositories.UserRepository;
import me.tucao.services.ImageService;
import me.tucao.vo.AjaxResult;
import me.tucao.vo.BindingErrors;
import me.tucao.vo.ImageReadyVo;
import me.tucao.vo.ValidationEngineError;
import me.tucao.vo.formbean.UserAvatarFormBean;
import me.tucao.vo.formbean.UserBasicInfoFormBean;
import me.tucao.vo.formbean.UserPwdChangeFormBean;
import me.tucao.web.utils.SessionUtil;
import me.tucao.web.utils.WebImageUtil;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes(ApplicationConstants.SESSION_SIGNIN_USER)
public class ChangeUserSettingController implements ApplicationContextAware {

	private ApplicationContext ac;
	private Logger logger = LoggerFactory
			.getLogger(ChangeUserSettingController.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SessionUtil sessionUtil;
	@Autowired
	private ImageService imageService;
	@Autowired
	private ResourceRepository resourceRepository;

	@RequestMapping(value = "/setting", method = RequestMethod.GET)
	public String setting(Model model, HttpSession session) {
		if (sessionUtil.getSignInUser(session) == null) {
			return "redirect:/signin";
		}
		return "profiles/setting";
	}

	@RequestMapping(value = "/setting/basic", method = RequestMethod.POST)
	public @ResponseBody
	AjaxResult submitBasic(@Valid UserBasicInfoFormBean formBean,
			BindingResult result, ModelAndView mav, HttpSession session) {
		User signInUser = sessionUtil.getSignInUser(session);
		if (signInUser == null) {
			return new AjaxResult(AjaxResultCode.NEED_SIGNIN);
		}
		if (result.hasErrors()) {
			return new AjaxResult(AjaxResultCode.INVALID,
					BindingErrors.from(result));
		}
		signInUser.setName(formBean.getName());
		signInUser.setCity(formBean.getCity());
		Gender gender = null;
		try {
			gender = Gender.valueOf(formBean.getGender());
		} catch (IllegalArgumentException iae) {
			logger.warn("Get illegal gender \"" + formBean.getGender()
					+ "\" for user" + signInUser.getName());
		}
		signInUser.setGender(gender);
		signInUser.setSummary(formBean.getSummary());
		userRepository.save(signInUser);
		return new AjaxResult(AjaxResultCode.SUCCESS);
	}

	@RequestMapping(value = "/setting/basic/validate", method = RequestMethod.POST)
	public @ResponseBody
	Object[] validateSubmitBasic(@Valid UserBasicInfoFormBean formBean,
			BindingResult result, ModelAndView mav, HttpSession session) {
		String name = formBean.getName();
		User signInUser = sessionUtil.getSignInUser(session);
		if (!signInUser.getName().equals(name)) {
			User user = userRepository.getByName(name);
			if (user != null) {
				result.addError(new FieldError("formBean", "name",
						"这个昵称太抢手了,换一个吧"));
			}
		}
		if (result.hasErrors()) {
			return ValidationEngineError.normalize(ValidationEngineError
					.from(result));
		} else {
			return new ValidationEngineError[] {};
		}
	}

	@RequestMapping(value = "/setting/changepwd", method = RequestMethod.POST)
	public @ResponseBody
	AjaxResult changePwd(@Valid UserPwdChangeFormBean formBean,
			BindingResult result, ModelAndView mav, HttpSession session) {
		User signInUser = sessionUtil.getSignInUser(session);
		if (signInUser == null) {
			return new AjaxResult(AjaxResultCode.NEED_SIGNIN);
		}
		if (formBean.getOldPwd() != null
				&& !formBean.getOldPwd().equals(signInUser.getPassword())) {
			result.addError(new FieldError("formBean", "oldPwd", "密码不正确"));
		}
		if (result.hasErrors()) {
			return new AjaxResult(AjaxResultCode.INVALID,
					BindingErrors.from(result));
		}
		signInUser.setPassword(formBean.getNewPwd());
		userRepository.save(signInUser);
		return new AjaxResult(AjaxResultCode.SUCCESS);
	}

	@RequestMapping(value = "/setting/changepwd/validate", method = RequestMethod.POST)
	public @ResponseBody
	Object[] validateChangePwd(@Valid UserPwdChangeFormBean formBean,
			BindingResult result, ModelAndView mav, HttpSession session) {
		User signInUser = sessionUtil.getSignInUser(session);
		if (formBean.getOldPwd() != null
				&& !formBean.getOldPwd().equals(signInUser.getPassword())) {
			result.addError(new FieldError("formBean", "oldPwd", "密码不正确"));
		}
		if (result.hasErrors()) {
			return ValidationEngineError.normalize(ValidationEngineError
					.from(result));
		} else {
			return new ValidationEngineError[] {};
		}
	}

	@RequestMapping(value = "/setting/avatar", method = RequestMethod.POST)
	public @ResponseBody
	AjaxResult changeAvatar(@Valid UserAvatarFormBean formBean,
			BindingResult result, ModelAndView mav, HttpSession session) {
		User signInUser = sessionUtil.getSignInUser(session);
		if (signInUser == null) {
			return new AjaxResult(AjaxResultCode.NEED_SIGNIN);
		}
		if (result.hasErrors()) {
			return new AjaxResult(AjaxResultCode.INVALID,
					BindingErrors.from(result));
		}
		try {
			int idx = formBean.getImageUrl().lastIndexOf("/");
			org.springframework.core.io.Resource resource = ac
					.getResource(ApplicationConfig.uploadTempRepository + "/"
							+ formBean.getImageUrl().substring(idx + 1));
			File file = resource.getFile();
			String ext = null;
			if (file != null && ext == null) {
				ext = FilenameUtils.getExtension(file.getName());
			}
			BufferedImage orgImg = ImageIO.read(file);
			// save original avatar file
			String resId = imageService.put(file);
			Resource res = new Resource();
			res.setOrgSize(new Integer[]{ orgImg.getHeight(), orgImg.getWidth() });
			res.setResId(resId);
			res.setExt(ext);
			resourceRepository.save(res);
			signInUser.setAvatarOrg(res);
			// save avatar file
			BufferedImage avatarImg = orgImg.getSubimage(formBean.getX(),
					formBean.getY(), formBean.getW(), formBean.getH());
			ImageIO.write(avatarImg, ext, file);
			resId = imageService.put(file);
			res = new Resource();
			res.setOrgSize(new Integer[] { avatarImg.getHeight(), avatarImg.getWidth() });
			res.setResId(resId);
			res.setExt(ext);
			resourceRepository.save(res);
			signInUser.setAvatar(res);
			userRepository.save(signInUser);
		} catch (Exception e) {
			return new AjaxResult(AjaxResultCode.EXCEPTION, e.getMessage());
		}
		return new AjaxResult(AjaxResultCode.SUCCESS);
	}

	public void setApplicationContext(ApplicationContext ac)
			throws BeansException {
		this.ac = ac;
	}
}

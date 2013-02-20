package me.tucao.web.mvc.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import me.tucao.constants.ActivityType;
import me.tucao.constants.AjaxResultCode;
import me.tucao.constants.ApplicationConstants;
import me.tucao.domains.Activity;
import me.tucao.domains.Forward;
import me.tucao.domains.Spot;
import me.tucao.domains.User;
import me.tucao.exceptions.ResourceNotFoundException;
import me.tucao.repositories.ActivityRepository;
import me.tucao.repositories.ForwardRepository;
import me.tucao.repositories.SpotRepository;
import me.tucao.repositories.UserRepository;
import me.tucao.vo.AjaxResult;
import me.tucao.vo.BindingErrors;
import me.tucao.vo.ForwardFormBean;
import me.tucao.web.utils.AjaxUtil;
import me.tucao.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/ops")
@SessionAttributes(ApplicationConstants.SESSION_SIGNIN_USER)
public class AddForwardController {
	
	@Autowired
	ForwardRepository forwardRepository;
	@Autowired
	ActivityRepository activityRepository;
	@Autowired
	SpotRepository spotRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SessionUtil sessionUtil;
	@Autowired
	private AjaxUtil ajaxUtil;
	
	@RequestMapping(value="/forward/create", method=RequestMethod.POST)
	public @ResponseBody AjaxResult create(
			@Valid ForwardFormBean bean,
			@ModelAttribute(ApplicationConstants.SESSION_SIGNIN_USER) 
				User signInUser,
			BindingResult result,
			Model model, HttpServletRequest request, HttpSession session){
		if(!ajaxUtil.isAjaxRequest(request)){
			throw new ResourceNotFoundException();
		}
		if(result.hasErrors()){
			return new AjaxResult(AjaxResultCode.INVALID, 
					BindingErrors.from(result));
		}
		Activity activity = activityRepository.findOne(bean.getActId());
		if(activity==null){
			throw new RuntimeException("Invalid activity id:" + bean.getActId());
		}
		if(activity.getTargetSpot() == null || 
				(ActivityType.SPOT != activity.getType() &&
					ActivityType.FORWARD != activity.getType())){
			throw new RuntimeException("Invalid activity type:" + activity.getType());
		}
		Spot target = spotRepository.findOne(activity.getTargetSpot());
	
		// save forward as comment
		Forward fwd = Forward.from(bean, signInUser);
		fwd.setAct(activity);
		fwd = forwardRepository.save(fwd);
		
		// incr forwarded count of original activity
		activityRepository.inc(activity.getId(), "forwardedCount", 1);
		
		// save forward activity
		activity = new Activity();
		activity.setOwner(signInUser.getId());
		activity.setCreatedAt(new Date());
		activity.setTargetSpot(target.getId());
		activity.setContent(bean.getContent());
		activity.setType(ActivityType.FORWARD);
		activity.setBasedOn(bean.getActId());
		activity.setBy(sessionUtil.getBy(session));
		activityRepository.save(activity);
		
		// incr forwarded count of target spot
		spotRepository.inc(target.getId(), "forwardedCount", 1);
		
		// incr forward count of sign in user
		userRepository.inc(signInUser.getId(), "forwardCount", 1);
		
		return new AjaxResult(AjaxResultCode.SUCCESS, fwd);
	}
}
package me.tucao.web.mvc.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.tucao.constants.ActivityType;
import me.tucao.constants.AjaxResultCode;
import me.tucao.constants.ApplicationConstants;
import me.tucao.domains.Activity;
import me.tucao.domains.FollowShip;
import me.tucao.domains.User;
import me.tucao.exceptions.ResourceNotFoundException;
import me.tucao.exceptions.UnauthorizedOperationException;
import me.tucao.repositories.ActivityRepository;
import me.tucao.repositories.FollowShipRepository;
import me.tucao.repositories.UserRepository;
import me.tucao.vo.AjaxResult;
import me.tucao.web.utils.AjaxUtil;
import me.tucao.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/ops")
@SessionAttributes(ApplicationConstants.SESSION_SIGNIN_USER)
public class ChangeFollowShipController {
	
	@Autowired
	FollowShipRepository followShipRepository;
	@Autowired
	ActivityRepository activityRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	SessionUtil sessionUtil;
	@Autowired
	private AjaxUtil ajaxUtil;
	
	@RequestMapping(value="/follow/{targetId}", method=RequestMethod.GET)
	public @ResponseBody AjaxResult follow(@PathVariable String targetId,
			@ModelAttribute(ApplicationConstants.SESSION_SIGNIN_USER) User signInUser,
			Model model, HttpServletRequest request, HttpSession session){
		if(!ajaxUtil.isAjaxRequest(request)){
			throw new ResourceNotFoundException();
		}
		User target = userRepository.findOne(targetId);
		if(target==null){
			throw new RuntimeException("Invalid user id : " + targetId);
		}
		FollowShip fs = followShipRepository.getByTargetAndFollowed(targetId, signInUser.getId());
		boolean followed = false;
		if(fs!=null && fs.getStatus() == ApplicationConstants.FOLLOWSHIP_DISABLED){
			followed = true;
			fs.setStatus(ApplicationConstants.FOLLOWSHIP_NORMAL);
			fs.setUpdatedAt(new Date());
			userRepository.inc(signInUser.getId(), "followCount", 1);
			userRepository.inc(target.getId(), "fansCount", 1);
		}else if(fs==null){
			followed = true;
			fs = new FollowShip();
			fs.setCreatedAt(new Date());
			fs.setUpdatedAt(fs.getCreatedAt());
			fs.setTarget(target);
			fs.setFollowed(signInUser);
			fs.setStatus(ApplicationConstants.FOLLOWSHIP_NORMAL);
			userRepository.inc(signInUser.getId(), "followCount", 1);
			userRepository.inc(target.getId(), "fansCount", 1);
		}
		if(followed){
			followShipRepository.save(fs);
			// save activity
			Activity activity = new Activity();
			activity.setOwner(signInUser.getId());
			activity.setCreatedAt(new Date());
			activity.setTargetUser(target.getId());
			activity.setType(ActivityType.FOLLOW);
			activity.setBy(sessionUtil.getBy(session));
			activityRepository.save(activity);
		}
		return new AjaxResult(AjaxResultCode.SUCCESS);
	}
	
	@RequestMapping(value="/defollow/{targetId}", method=RequestMethod.GET)
	public @ResponseBody AjaxResult defollow(@PathVariable String targetId,
			@ModelAttribute(ApplicationConstants.SESSION_SIGNIN_USER) User signInUser,
			Model model, HttpServletRequest request, 
			HttpSession session){
		if(!ajaxUtil.isAjaxRequest(request)){
			throw new ResourceNotFoundException();
		}
		if(signInUser==null){
			throw new UnauthorizedOperationException();
		}
		User target = userRepository.findOne(targetId);
		if(target==null){
			throw new RuntimeException("Invalid user id : " + targetId);
		}
		FollowShip fs = followShipRepository.getByTargetAndFollowed(targetId, signInUser.getId());
		if(fs!=null && fs.getStatus() == ApplicationConstants.FOLLOWSHIP_NORMAL){
			fs.setStatus(ApplicationConstants.FOLLOWSHIP_DISABLED);
			fs.setUpdatedAt(new Date());
			userRepository.inc(signInUser.getId(), "followCount", -1);
			userRepository.inc(target.getId(), "fansCount", -1);
			followShipRepository.save(fs);
		}
		return new AjaxResult(AjaxResultCode.SUCCESS);
	}
}

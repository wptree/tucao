package me.tucao.web.mvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.tucao.domains.User;
import me.tucao.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/profiles")
public class ViewProfileBasicInfoController {
	
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(value="/{id}/basic", method=RequestMethod.GET)
	public String view(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		
		User user = userRepository.findOne(id);
		model.addAttribute("user", user);
		return "profiles/basic";
	}
}

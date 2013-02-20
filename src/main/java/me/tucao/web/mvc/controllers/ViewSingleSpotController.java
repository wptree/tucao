package me.tucao.web.mvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.tucao.domains.Spot;
import me.tucao.repositories.SpotRepository;
import me.tucao.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/spots")
public class ViewSingleSpotController {

	@Autowired
	SpotRepository spotRepository;
	@Autowired
	SessionUtil sessionUtil;
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public String view(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		
		Spot spot = spotRepository.findOne(id);
		model.addAttribute("spot", spot);
		return "spots/single";
	}
	
}

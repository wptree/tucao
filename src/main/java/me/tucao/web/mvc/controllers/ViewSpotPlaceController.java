package me.tucao.web.mvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.tucao.domains.Place;
import me.tucao.repositories.PlaceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/spots")
public class ViewSpotPlaceController {

	@Autowired
	PlaceRepository placeRepository;

	@RequestMapping(value="/place/{id}", method=RequestMethod.GET)
	public String view(@PathVariable String id, Model model, 
			HttpServletRequest request, HttpSession session){
		Place place = placeRepository.findOne(id);
		model.addAttribute("place", place);
		return "spots/place";
	}
	
}

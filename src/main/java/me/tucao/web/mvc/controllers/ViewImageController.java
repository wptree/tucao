package me.tucao.web.mvc.controllers;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.tucao.domains.Resource;
import me.tucao.repositories.ResourceRepository;
import me.tucao.services.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/images")
public class ViewImageController {

	@Autowired
	ImageService imageService; 
	@Autowired
	ResourceRepository resourceRepository;
	@Autowired
	ServletContext context;
	
	@RequestMapping(value="/spots/{id}", method=RequestMethod.GET)
	public void viewSpot(@PathVariable String id,
			Model model, HttpServletRequest request, 
			HttpServletResponse response,
			HttpSession session){
		try {
			Resource resource = resourceRepository.getByResId(id);
			if (resource != null) {
				String mime = context.getMimeType(
						"mock." + resource.getExt());
				response.setContentType(mime != null ? mime : "image/jpeg");
			}
			ServletOutputStream os = response.getOutputStream();
			imageService.get(id, os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/avatars/{id}", method=RequestMethod.GET)
	public void viewAvatar(@PathVariable String id,
			Model model, HttpServletRequest request, 
			HttpServletResponse response,
			HttpSession session){
		try {
			Resource resource = resourceRepository.getByResId(id);
			if (resource != null) {
				String mime = context.getMimeType(
						"mock." + resource.getExt());
				response.setContentType(mime != null ? mime : "image/jpeg");
			}
			ServletOutputStream os = response.getOutputStream();
			imageService.get(id, os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

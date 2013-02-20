package me.tucao.web.mvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.tucao.constants.AjaxResultCode;
import me.tucao.constants.ApplicationConfig;
import me.tucao.domains.CityMeta;
import me.tucao.exceptions.ResourceNotFoundException;
import me.tucao.repositories.CityMetaRepository;
import me.tucao.vo.AjaxResult;
import me.tucao.web.utils.AjaxUtil;
import me.tucao.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GetCityMetaController {
	
	@Autowired
	CityMetaRepository cityMetaRepository;
	@Autowired
	AjaxUtil ajaxUtil;
	@Autowired
	SessionUtil sessionUtil;
	
	@RequestMapping(value="/citymeta", method=RequestMethod.GET)
	public @ResponseBody AjaxResult meta(HttpServletRequest request, 
			ModelAndView mav, HttpSession session){
		if(!ajaxUtil.isAjaxRequest(request)){
			throw new ResourceNotFoundException();
		}
		CityMeta city = sessionUtil.getGeoCityMeta(session);
		return city != null ? new AjaxResult(AjaxResultCode.SUCCESS, city) : 
			meta(null, request, mav, session);
	}
	
	@RequestMapping(value="/citymeta/{pinyin}", method=RequestMethod.GET)
	public @ResponseBody AjaxResult meta(@PathVariable String pinyin,
			HttpServletRequest request, ModelAndView mav, HttpSession session){
		if(!ajaxUtil.isAjaxRequest(request)){
			throw new ResourceNotFoundException();
		}
		String py = pinyin;
		if(!StringUtils.hasText(pinyin)){
			py = ApplicationConfig.defaultCityPinyin;
		}
		CityMeta cityMeta = cityMetaRepository.getByPinyin(py);
		if(cityMeta == null){
			cityMeta = cityMetaRepository
					.getByPinyin(ApplicationConfig.defaultCityPinyin);
		}
		return new AjaxResult(AjaxResultCode.SUCCESS, cityMeta);
	}
	
}

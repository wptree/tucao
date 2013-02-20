package me.tucao.web.mvc.controllers;

import javax.servlet.http.HttpSession;

import me.tucao.constants.AjaxResultCode;
import me.tucao.constants.ApplicationConstants;
import me.tucao.domains.CityMeta;
import me.tucao.repositories.CityMetaRepository;
import me.tucao.vo.AjaxResult;
import me.tucao.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UpdateSelectedCityController {

	@Autowired
	private CityMetaRepository cityMetaRepository;
	@Autowired
	SessionUtil sessionUtil;
	
	@RequestMapping(value="/geocity/{city}", method=RequestMethod.GET)	
	public @ResponseBody AjaxResult update(@PathVariable String city, 
			HttpSession session){
		CityMeta cityMeta = cityMetaRepository.getByPinyin(city);
		if(cityMeta !=null){
			session.setAttribute(ApplicationConstants.SESSION_SELECTED_CITY_META, 
				cityMeta);
		}else{
			cityMeta = sessionUtil.getGeoCityMeta(session);
		}
		return new AjaxResult(AjaxResultCode.SUCCESS, cityMeta);
	}
}

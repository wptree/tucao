package me.tucao.web.mvc.controllers;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import me.tucao.constants.ActivityType;
import me.tucao.constants.AjaxResultCode;
import me.tucao.constants.ApplicationConfig;
import me.tucao.domains.Activity;
import me.tucao.domains.Comment;
import me.tucao.domains.Spot;
import me.tucao.exceptions.ResourceNotFoundException;
import me.tucao.repositories.ActivityRepository;
import me.tucao.repositories.CityMetaRepository;
import me.tucao.repositories.CommentRepository;
import me.tucao.repositories.SpotRepository;
import me.tucao.repositories.UserRepository;
import me.tucao.vo.AjaxResult;
import me.tucao.vo.MarkerVo;
import me.tucao.vo.PinVo;
import me.tucao.web.utils.AjaxUtil;
import me.tucao.web.utils.MapUtil;
import me.tucao.web.utils.SessionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/spots")
public class SearchSpotsController {
	private static final Logger logger = 
			LoggerFactory.getLogger(SearchSpotsController.class);
	@Autowired
	private CityMetaRepository cityMetaRepository;
	@Autowired
	private SpotRepository spotRepository;
	@Autowired
	private ActivityRepository activityRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private AjaxUtil ajaxUtil;
	@Autowired
	MapUtil mapUtil;
	@Autowired
	SessionUtil sessionUtil;
	
	@RequestMapping(value="/search/list", method=RequestMethod.GET)
	public String search(Model model, HttpServletRequest request){
		Iterable<Spot> spots = doSearch(request, false);
		Collection<PinVo> pins = new ArrayList<PinVo>();
		if(spots!=null){
			Pageable pageable = new PageRequest(0, 
					ApplicationConfig.pinCmtPageSize, 
					new Sort(new Order(Direction.DESC, "createdAt")));
			for(Spot spot : spots){
				try{
					Activity act = activityRepository.getByOwnerAndTargetSpotAndType(
							spot.getCreatedBy().getId(), spot.getId(), ActivityType.SPOT.name());
					Page<Comment> cmts = null;
					if(act!=null){
						 cmts = commentRepository.findByAct(
								act.getId(), pageable);
					}
					if(spot.getPlace()==null){
						pins.add(PinVo.from(spot,
								cityMetaRepository.getByPinyin(StringUtils.hasText(spot.getCity())?
										spot.getCity():ApplicationConfig.defaultCityPinyin), act, cmts));
					}else{
						pins.add(PinVo.from(spot,null, act, cmts));
					}
				}catch(Exception e){
					logger.warn(e.getMessage(), e);
				}
			}
		}
		logger.info("**** Get " + (pins!=null?pins.size():0)+" spots");
		model.addAttribute("pins", pins);
		return "spots/list";
	}
	
	private Iterable<Spot> doSearch(HttpServletRequest request, boolean isMarker){
		String city = request.getParameter("city");
		String category = request.getParameter("category");
		String summaryLike = request.getParameter("keyword");
		String no = request.getParameter("no");
		logger.info("city:" + city);
		logger.info("category:" + category);
		logger.info("keyword:" + summaryLike);
		logger.info("no:" + no);
		int pageNo = 0;
		try{
			pageNo = Integer.parseInt(no);
		}catch(NumberFormatException nfe){
			logger.info("paramater page no is invalid, will use 0 as default.");
		}
		Pageable pageable = new PageRequest(Math.max(pageNo, 0), 
				ApplicationConfig.masonryPageSize, 
				new Sort(new Order(Direction.DESC, "createdAt")));
		if(ApplicationConfig.defaultCityPinyin.equals(city)){
			city = "";
		}
		
		Iterable<Spot> spots = null;
		if(!isMarker){
			spots = spotRepository.search(StringUtils.trimWhitespace(city), 
					StringUtils.trimWhitespace(category), 
						StringUtils.trimWhitespace(summaryLike), pageable);
		}else{
			spots = spotRepository.searchMarker( StringUtils.trimWhitespace(city), 
					StringUtils.trimWhitespace(category), 
						StringUtils.trimWhitespace(summaryLike), pageable);
		}
		return spots;
	}
	
	@RequestMapping(value="/search/marker", method=RequestMethod.GET)
	public @ResponseBody AjaxResult marker(Model model, HttpServletRequest request){
		if(!ajaxUtil.isAjaxRequest(request)){
			throw new ResourceNotFoundException();
		}
		Iterable<Spot> spots = doSearch(request, true);
		Collection<MarkerVo> markers = new ArrayList<MarkerVo>();
		if(spots!=null){
			for(Spot spot : spots){
				MarkerVo vo = MarkerVo.from(spot);
				if(vo!=null){
					markers.add(MarkerVo.from(spot));
				}
			}
		}
		return new AjaxResult(AjaxResultCode.SUCCESS, markers);
	}
}
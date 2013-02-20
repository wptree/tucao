package me.tucao.web.mvc.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.tucao.constants.ApplicationConfig;
import me.tucao.constants.ApplicationConstants;
import me.tucao.constants.ByType;
import me.tucao.web.utils.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ByInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private SessionUtil sessionUtil;
	
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler)
		    throws Exception {
		HttpSession session = request.getSession();
		ByType by = sessionUtil.getBy(session);
		if(by == null){
			String byStr = request.getHeader(ApplicationConstants.HEADER_BY);
			if(byStr == null){
				byStr = ApplicationConfig.defaultBy;
			}
			by = ByType.valueOf(byStr.toUpperCase());
			session.setAttribute(ApplicationConstants.SESSION_BY, 
					by);
		}
		return true;
	}
	
}

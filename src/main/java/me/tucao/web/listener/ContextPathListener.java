package me.tucao.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import me.tucao.constants.ApplicationConfig;

public class ContextPathListener implements ServletContextListener {

	
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
        sc.removeAttribute("base");
        ApplicationConfig.base = null;
	}

	public void contextInitialized(ServletContextEvent sce) {
		ServletContext sc = sce.getServletContext();
        sc.setAttribute("base", getContextPath(sc)); 
        ApplicationConfig.base = getContextPath(sc);
	}

	private String getContextPath(ServletContext sc) {
        return sc.getContextPath();
    }
}

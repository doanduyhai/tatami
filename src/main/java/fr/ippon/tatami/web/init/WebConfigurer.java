package fr.ippon.tatami.web.init;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

import org.springframework.mobile.device.DeviceResolverHandlerFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import fr.ippon.tatami.config.ApplicationConfiguration;
import fr.ippon.tatami.config.DispatcherServletConfig;
import fr.ippon.tatami.web.monitoring.MonitoringFilter;

public class WebConfigurer implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		ServletContext servletContext = sce.getServletContext();
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(ApplicationConfiguration.class);
		rootContext.refresh();

		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, rootContext);

		AnnotationConfigWebApplicationContext dispatcherServletConfig = new AnnotationConfigWebApplicationContext();
		dispatcherServletConfig.setParent(rootContext);
		dispatcherServletConfig.register(DispatcherServletConfig.class);

		ServletRegistration.Dynamic dispatcherServlet = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherServletConfig));
		dispatcherServlet.addMapping("/*");
		dispatcherServlet.setLoadOnStartup(1);

		FilterRegistration.Dynamic springSecurityFilter = servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy());
		EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
		springSecurityFilter.addMappingForServletNames(disps, true, "dispatcher");

		FilterRegistration.Dynamic monitoringFilter = servletContext.addFilter("monitoringFilter", new MonitoringFilter());
		monitoringFilter.addMappingForUrlPatterns(disps, true, "/*");

		FilterRegistration.Dynamic springMobileFilter = servletContext.addFilter("springMobileFilter", new DeviceResolverHandlerFilter());
		springMobileFilter.addMappingForUrlPatterns(disps, true, "/*");

		servletContext.getSessionCookieConfig().setMaxAge(10);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		// TODO : close ApplicationContexts
	}
}

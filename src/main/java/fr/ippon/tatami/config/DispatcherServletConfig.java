package fr.ippon.tatami.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import fr.ippon.tatami.web.converter.JacksonViewAwareHttpMessageConverter;
import fr.ippon.tatami.web.converter.UserConverter;
import fr.ippon.tatami.web.interceptor.SecurityInterceptor;

@Configuration
@ComponentScan(basePackages = "fr.ippon.tatami.web")
public class DispatcherServletConfig extends WebMvcConfigurationSupport
{

	@Bean
	public ContentNegotiatingViewResolver viewResolver()
	{
		ContentNegotiatingViewResolver contentNegociationResolver = new ContentNegotiatingViewResolver();
		Map<String, String> mediaTypes = new HashMap<String, String>();

		mediaTypes.put("html", MediaType.TEXT_HTML_VALUE);
		mediaTypes.put("json", MediaType.APPLICATION_JSON_VALUE);

		contentNegociationResolver.setMediaTypes(mediaTypes);

		BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
		beanNameViewResolver.setOrder(2);

		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		ServletContextTemplateResolver thymeleafTemplateResolver = new ServletContextTemplateResolver();
		thymeleafTemplateResolver.setPrefix("/");
		thymeleafTemplateResolver.setSuffix(".html");
		thymeleafTemplateResolver.setTemplateMode("HTML5");
		thymeleafTemplateResolver.setCacheable(false);

		SpringTemplateEngine thymeleafTemplateEngine = new SpringTemplateEngine();
		thymeleafTemplateEngine.setTemplateResolver(thymeleafTemplateResolver);

		thymeleafViewResolver.setTemplateEngine(thymeleafTemplateEngine);
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		thymeleafViewResolver.setOrder(1);
		thymeleafViewResolver.setContentType(MediaType.TEXT_HTML_VALUE);

		MappingJacksonJsonView jsonView = new MappingJacksonJsonView();

		contentNegociationResolver.setViewResolvers(Arrays.asList((ViewResolver) thymeleafViewResolver));
		contentNegociationResolver.setDefaultViews(Arrays.asList((View) jsonView));

		return contentNegociationResolver;
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");
	}

	@Override
	protected void addInterceptors(InterceptorRegistry registry)
	{
		SecurityInterceptor securityInterceptor = new SecurityInterceptor();
		registry.addInterceptor(securityInterceptor);
	}

	@Override
	protected void addFormatters(FormatterRegistry registry)
	{
		UserConverter userConverter = new UserConverter();
		registry.addConverter(userConverter);
	}

	// @Override
	// protected void configureMessageConverters(List<HttpMessageConverter<?>> converters)
	// {
	// StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
	// stringConverter.setWriteAcceptCharset(false);
	//
	// converters.add(new ByteArrayHttpMessageConverter());
	// converters.add(stringConverter);
	// converters.add(new ResourceHttpMessageConverter());
	// converters.add(new SourceHttpMessageConverter<Source>());
	// converters.add(new XmlAwareFormHttpMessageConverter());
	// converters.add(new JacksonViewAwareHttpMessageConverter());
	//
	// }

	@Bean(name = "jacksonViewAwareHttpMessageConverter")
	public JacksonViewAwareHttpMessageConverter jacksonViewAwareHttpMessageConverter()
	{
		return new JacksonViewAwareHttpMessageConverter();
	}
}

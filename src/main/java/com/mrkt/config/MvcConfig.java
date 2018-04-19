package com.mrkt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mrkt.authorization.interceptor.AuthorizationInterceptor;

@Configuration
@Component
public class MvcConfig extends WebMvcConfigurerAdapter{

	@Autowired
	private AuthorizationInterceptor authorizationInterceptor;
	
	@Bean
	public AuthorizationInterceptor getAuthorizationInterceptor() {
		return new AuthorizationInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authorizationInterceptor);
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// 这里匹配了所有的URL，允许所有的外域发起跨域请求，允许外域发起请求任意HTTP Method，允许跨域请求包含任意的头信息。
		registry.addMapping("/**")
		        .allowedHeaders("*")
		        .allowedMethods("*")
		        .allowedOrigins("*")
		        .allowCredentials(true);
	}
	
}

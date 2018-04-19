package com.mrkt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @ClassName	CommonProperties
 * @Description
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/17 07:42:11
 */
@Configuration
@PropertySource("classpath:staticconfig.properties")
public class CommonProperties {

	/** 项目的域名地址 */
	@Value("${common.project.url}")
	private String projectUrl;
	
	/** 图片在主机上的相对地址，存储到数据库中 */
	@Value("${common.image.url}")
	private String imageUrl;
	
	/** 图片在主机上存储的父子目录绝对地址 */
	@Value("${common.image.root}")
	private String rootpath;

	public String getProjectUrl() {
		return projectUrl;
	}

	public void setProjectUrl(String projectUrl) {
		this.projectUrl = projectUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getRootpath() {
		return rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}
	
}

package com.mrkt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @ClassName	CommonConfig
 * @Description
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/04/17 07:42:11
 */
@Configuration
@PropertySource("classpath:staticconfig.properties")
public class CommonConfig {

	/** 项目的域名地址 */
	@Value("${common.project.url}")
	private String projectUrl;
	
	/** 图片所存储的服务器url，存储到数据库中 */
	@Value("${common.image.url}")
	private String imageUrl;
	
	/** 图片在主机上存储的父子目录绝对地址 */
	@Value("${common.image.root}")
	private String rootpath;

	/** 七牛云AccessKey */
	@Value("${common.image.accesskey}")
	private String accessKey;
	
	/** 七牛云SecretKey */
	@Value("${common.image.secretKey}")
	private String secretKey;
	
	/** 七牛云存储空间名称 */
	@Value("${common.image.bucket}")
	private String bucket;
	
	
	
	
	
	
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

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	
	
}

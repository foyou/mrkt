package com.mrkt.api;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mrkt.sys.config.Configurator;

//@RestController
@Controller
public class HomeController {

	private Configurator cgr;
	private String wxRedirectUri;
	private String wxAppId;
	
	/** 项目链接 */
	private static final String PROJECT_URL = "http://scauymt.com/";
	/** 日志 */
	private static Logger logger = LoggerFactory.getLogger(HomeController.class);
	/** 微信公众平台提供的appid */
	private static final String WX_APP_ID = "wx.app.id";
	/** 在线上环境时Redirect_uri应该为前端应用地址 */
	private final static String WX_APP_REDIRECT_URI = "wx.app.redirect_uri";
	
	{
		cgr = Configurator.getInstance();
		
		this.wxAppId = cgr.get(WX_APP_ID);
		this.wxRedirectUri = cgr.get(WX_APP_REDIRECT_URI);
	}
	
//	@Authorization
	@RequestMapping(value = {"", "/"})
	public Object index(){
		return "index";
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/auth")
	public String auth(String returnUrl, HttpServletResponse response) throws IOException {
		logger.info("/auth_returnUrl: " + returnUrl);
		String state = returnUrl != null ? returnUrl: PROJECT_URL;
		return "redirect:" + 
				String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect", 
				this.wxAppId, URLEncoder.encode(this.wxRedirectUri), "snsapi_userinfo", state);
	}
	
}

package com.mrkt.wx.api;

import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrkt.authorization.core.TokenManager;
import com.mrkt.config.RedisConfig;
import com.mrkt.sys.config.Configurator;
import com.mrkt.usr.core.WxUserAction;
import com.mrkt.utils.CookieUtils;
import com.mrkt.vo.ReturnModel;
import com.mrkt.wx.core.HttpRequest;
import com.mrkt.wx.core.WxUserServiceImpl;
import com.mrkt.wx.model.WxAccessToken;
import com.mrkt.wx.model.WxUser;

//@RestController
@Controller
public class WxController {

	public final static String token;;
	private final static String  APPID;
	private final static String APPSRECT;
	@SuppressWarnings("unused")
	private final static String GRANT_TYPE;		
	@SuppressWarnings("unused")
	private static final String USER_CACHE = "user";
	/** 链接后缀 */
	private static final String SUFFIX = "#/";
	
	protected static Logger logger = LoggerFactory.getLogger(WxController.class);
	
	private HttpRequest httpRequest;
	
	private static Configurator cf;
	
	static{
		cf = Configurator.getInstance();
		cf.load();
		
		GRANT_TYPE = cf.get("wx.grant_type");
		APPSRECT = cf.get("wx.app.srect");
		APPID = cf.get("wx.app.id");
		token = cf.get("wx.token");
	}
	
	@Autowired
	@Qualifier("redisTokenManager")
	private TokenManager tokenManager;
	
	@Autowired
	WxUserServiceImpl wxUserServiceImpl;
	
	@Autowired
	@Qualifier("userActionImpl")
	WxUserAction userAction;

	@Autowired
	RedisConfig redisConfig;
	
	{
		httpRequest = HttpRequest.getInstance();
	}
	
	@RequestMapping("/wxx")
	@ResponseBody
	public Object test(
			@RequestParam(required=false, name="signature") String signature,
			@RequestParam(required=false, name="timestamp") String timestamp,
			@RequestParam(required=false, name="nonce") String nonce,
			@RequestParam(required=false, name="echostr") String echostr
			){
		String[] arr = new String[]{timestamp, nonce, token};
		StringBuffer temp = new StringBuffer();
		for (String string : arr) {
			temp.append(string);
		}
		return echostr;
	}
	
	@RequestMapping("/wxx/OAuth2/login")
//	public Object login(
	public String login(
			@RequestParam(name="code") String code,
			@RequestParam(name="state", required=false) String state,
			HttpServletResponse servletResponse
			){
		String srect = null;
		Long uid = null;
		logger.info("-----------------state=" + state);
		if (!state.contains(SUFFIX)) state += SUFFIX;
		
		String response = httpRequest.doGet("https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APPID+"&secret="+APPSRECT+"&code="+code+"&grant_type=authorization_code");
		WxUser wxuser = null;
		WxAccessToken accessToken = JSONObject.parseObject(response, WxAccessToken.class);
		if (accessToken.getOpenid()!=null) {
			wxuser = wxUserServiceImpl.get(accessToken.getOpenid());
			if (wxuser!=null &&( userAction.login(wxuser))) {
// TODO
//				return wxuser;
//				return ReturnModel.SUCCESS(tokenManager.create(wxuser.getMrktUser()));
				srect = tokenManager.create(wxuser.getMrktUser()).getSrect();
				uid = wxuser.getMrktUser().getUid();
				
				return "redirect:" + state + "?srect=" + srect + "&uid=" + uid;
			}
		}
		if (accessToken.getAccess_token() != null && accessToken.getOpenid()!=null) {
			String resp = httpRequest.doGet("https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken.getAccess_token()+"&openid="+accessToken.getOpenid()+"&lang=zh_CN");
			wxuser = JSON.parseObject(resp, WxUser.class);
			logger.info(wxuser.toString());
			userAction.register(wxuser);
		}
		
		if (wxuser==null||wxuser.getNickName()==null) {
			srect = tokenManager.create(wxuser.getMrktUser()).getSrect();
			uid = wxuser.getMrktUser().getUid();
		}
		servletResponse.addHeader("srect", srect);
		return "redirect:" + state + "?srect=" + srect + "&uid=" + uid;

//		return (wxuser==null||wxuser.getNickName()==null)? 
//				null : ReturnModel.SUCCESS(tokenManager.create(wxuser.getMrktUser()));
	}
}

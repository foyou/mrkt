package com.mrkt.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName	IpUtils
 * @Description
 * @author		hdonghong
 * @version 	v1.0
 * @since		2018/05/04 12:34:58
 */
public class IpUtils {

	/**
	 * 获取请求的ip
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
	    String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
	    return ip;
	}
}

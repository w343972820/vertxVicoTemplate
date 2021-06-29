package com.binance.api.client.mainVico;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.util.StringUtils;

import io.vertx.core.http.HttpServerRequest;

//import javax.servlet.http.HttpServletRequest;

/**
 * IP地址
 */
public class IPUtils {
	private static Logger logger = LoggerFactory.getLogger(IPUtils.class);

	/**
	 * 获取IP地址
	 * HttpServletRequest
	 */
	/*public static String getIpAddr(HttpServletRequest request) {
    	String ip = null;
        try {
            ip = request.getHeader("X-Real-IP");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
        	logger.error("IPUtils ERROR ", e);
        }
        
        return ip;
    }
	*/
	
	/**
	 * 获取IP地址
	 * HttpServletRequest
	 */
	public static String getIpAddr(HttpServerRequest  request) {
    	String ip = null;
        try {
            ip = request.getHeader("X-Real-IP");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.remoteAddress().host();
            }
        } catch (Exception e) {
        	logger.error("IPUtils ERROR ", e);
        } 
        return ip;
    }
}

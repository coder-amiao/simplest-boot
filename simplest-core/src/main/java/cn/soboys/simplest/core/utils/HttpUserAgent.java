package cn.soboys.simplest.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.http.useragent.UserAgent;
import org.dromara.hutool.http.useragent.UserAgentUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/2 11:40
 * @webSite https://github.com/coder-amiao
 */
@Slf4j
public class HttpUserAgent {
    private static final String UNKNOWN = "unknown";


    /**
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * @return 请求头信息
     */
    public static String getDevice() {
        HttpServletRequest request = getRequest();
        String uaStr = request.getHeader("User-Agent");
        UserAgent ua = UserAgentUtil.parse(uaStr);
        return uaStr;
    }

    /**
     * @return 请求头信息
     */
    public static String getDeviceBrowser() {
        HttpServletRequest request = getRequest();
        String uaStr = request.getHeader("User-Agent");
        UserAgent ua = UserAgentUtil.parse(uaStr);
        String browser = ua.getBrowser().toString();
        return browser;
    }

    /**
     * @return 请求头信息
     */
    public static String getDeviceSystem() {
        HttpServletRequest request = getRequest();
        String uaStr = request.getHeader("User-Agent");
        UserAgent ua = UserAgentUtil.parse(uaStr);
        String platform = ua.getPlatform().toString();
        return platform;
    }

    public static String getIpAddr() {
        String ip = null;
        HttpServletRequest request = RequestUtil.getReq();
        String ipAddresses = request.getHeader("X-Real-IP");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("X-Forwarded-For");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }


}

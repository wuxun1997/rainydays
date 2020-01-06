package com.wlf.order.stub.aop;

import com.wlf.order.api.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器：记录日志
 *
 * @author wulinfeng
 * @since 2019/12/25
 */
@Slf4j
@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 日志记录
        log.info("request url : {}， remoteIp ： {}", getUrl(), IPUtil.getRemoteIp(httpServletRequest));

        return true;
    }


    /**
     * 获取当前请求的url
     *
     * @return
     */
    private String getUrl() {
        String url = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName()
                + ":" + httpServletRequest.getServerPort() + httpServletRequest.getServletPath();
        return url;
    }
}

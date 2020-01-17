package com.wlf.order.prize.aop;

import com.wlf.order.api.javabean.Result;
import com.wlf.order.api.util.IPUtil;
import com.wlf.order.api.util.OrderErrorCode;
import com.wlf.order.prize.service.MailService;
import com.wlf.order.prize.util.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 拦截器：增加重复请求的过滤、话单记录处理
 *
 * @author wulinfeng
 * @since 2019/12/25
 */
@Slf4j
@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    private final static SimpleDateFormat SF = new SimpleDateFormat("yyyyMMddHHmmss");

    // 话单格式：记录时间|接口名称|接口时延|调用方IP|本地IP|序列号|业务参数|结果码
    private final static String CDR_FORMAT = "{}|{}|{}|{}|{}|{}|{}|{}";

    // 时间戳缓存
    private final static TimeStampList cache = new TimeStampList(10000);

    @Autowired
    MailService mailService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 请求头校验
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        log.info("timestamp : {}, sign : {}", timestamp, sign);
        if (timestamp == null || timestamp.trim().equals("") || sign == null || sign.trim().equals("")) {
            Result result = new Result(OrderErrorCode.HEARD_EMPTY.getCode(),
                    OrderErrorCode.HEARD_EMPTY.getMsg());
            getResponse(result, response);
            return false;
        }

        // 时间戳校验
        if (timestamp.length() != 13) {
            Result result = new Result(OrderErrorCode.TIMESTAMP_ERROR.getCode(),
                    OrderErrorCode.TIMESTAMP_ERROR.getMsg());
            getResponse(result, response);
            return false;
        }

        Long requestTime = null;
        try {
            requestTime = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            Result result = new Result(OrderErrorCode.TIMESTAMP_ERROR.getCode(),
                    OrderErrorCode.TIMESTAMP_ERROR.getMsg());
            getResponse(result, response);
            return false;
        }

        // 日志添加追踪ID
        MDC.put("traceId", timestamp);

        // 重复请求校验
        if (cache.contains(requestTime)) {
            log.error("request timestamp repeat : {}", timestamp);
            Result result = new Result(OrderErrorCode.REPEAT_REQUEST.getCode(),
                    OrderErrorCode.REPEAT_REQUEST.getMsg());
            getResponse(result, response);
            return false;
        }

        cache.add(requestTime);

        // 获取请求和本地IP，记录话单
        String beginTime = String.valueOf(System.currentTimeMillis());
        String remoteIp = IPUtil.getRemoteIp(request);
        String localIp = IPUtil.getLocalIp();

        Map<String, String> strMap = new HashMap<>();
        strMap.put("beginTime", beginTime);
        strMap.put("remoteIp", remoteIp);
        strMap.put("localIp", localIp);
        strMap.put("sequence", timestamp);

        ThreadLocalUtil.setMap(strMap);

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {

        // 计算接口时延
        Map<String, String> strMap = ThreadLocalUtil.getMap();
        long beginTime = Long.parseLong(strMap.get("beginTime"));
        long currentTime = System.currentTimeMillis();

        // 获取当前时间
        String currentDate = SF.format(new Date(currentTime));

        // 异常发送邮件
        String mailContext = strMap.get("openPrize");
        if (mailContext != null && !mailContext.trim().equals("")) {
            mailService.sendSimpleMail("重要邮件需要你处理", mailContext);
        }

        // 记录话单
        log.error(CDR_FORMAT, currentDate, strMap.get("apiType"), currentTime - beginTime, strMap.get("remoteIp"),
                strMap.get("localIp"), strMap.get("sequence"), strMap.get("biz"), strMap.get("resultCode"));
    }

    /**
     * 构造响应消息体
     *
     * @param result
     * @param response
     * @throws IOException
     */
    private void getResponse(Result result, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        PrintWriter out = null;
        out = response.getWriter();
        out.write(BeanConvert.getResultJson(result));
        out.flush();
        out.close();
    }
}

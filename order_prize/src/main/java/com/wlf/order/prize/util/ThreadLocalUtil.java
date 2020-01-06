package com.wlf.order.prize.util;

import java.util.Map;

/**
 * 本地线程副本，用来存放话单业务字段
 *
 * @author wulinefng
 * @since 2019/12/25
 */
public class ThreadLocalUtil {
    private static final ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<>();

    /**
     * 获取线程副本
     * @return
     */
    public static Map<String, String> getMap() {
        return threadLocal.get();
    }

    /**
     * 设置线程副本
     * @param strMap
     */
    public static void setMap(Map<String, String> strMap) {
        threadLocal.set(strMap);
    }

    /**
     * 移除线程副本
     */
    public static void removeMap() {
        threadLocal.remove();
    }
}

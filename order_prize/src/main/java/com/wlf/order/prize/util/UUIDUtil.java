package com.wlf.order.prize.util;

import java.util.UUID;

/**
 * 32位UUID生成器
 */
public class UUIDUtil {
    public static String getSequence() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

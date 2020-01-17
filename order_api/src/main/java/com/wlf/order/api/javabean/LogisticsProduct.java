package com.wlf.order.api.javabean;

import lombok.*;

/**
 * 产品信息
 *
 * @author wulinfeng
 * @since 2020/1/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LogisticsProduct {
    // 受理产品编码
    private String productid;

    // 产品设备号
    private String equimentid;
}

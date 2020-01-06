package com.wlf.order.api.javabean;

import lombok.*;

import java.util.List;

/**
 * 退货信息
 * @author wulinfeng
 * @since  2019/12/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReturnOrder {

    // 订单Id
    private String orderId;

    // 用户手机号
    private String servernum;

    // 退货产品
    private List<ReturnProduct> productinfo;

}

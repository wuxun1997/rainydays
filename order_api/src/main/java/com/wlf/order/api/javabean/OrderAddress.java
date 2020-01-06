package com.wlf.order.api.javabean;

import lombok.*;

/**
 * 修改订单地址
 *
 * @author wulinfeng
 * @since 2019/12/18
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderAddress {

    private String orderId;

    private String address;
}

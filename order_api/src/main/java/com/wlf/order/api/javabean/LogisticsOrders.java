package com.wlf.order.api.javabean;

import lombok.*;

import java.util.List;

/**
 * 出库订单信息
 * @author wulinfeng
 * @since  2019/12/18
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LogisticsOrders {
    List<Logistics> orders;
    int orderCount;
}

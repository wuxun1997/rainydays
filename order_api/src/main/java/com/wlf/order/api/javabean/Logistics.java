package com.wlf.order.api.javabean;

import lombok.*;

import java.util.List;

/**
 * 物流信息
 * @author wulinfeng
 * @since  2019/12/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Logistics {

    // 订单Id
    private String orderId;

    // 产品信息
    private List<LogisticsProduct> products;

    // 用户手机号
    private String serviceNumber;

    // 物流单号
    private String logisticsOrderNumber;

    // 物流公司名称
    private String logisticsCompanyName;

    // 物流状态：0-未发货；1-发货中；2-已收货；3-已拒收
    private String logisticsStatus;

    // 装维状态：0-未安装；1-已安装
    private String installStatus;

    // 满意度
    private String satisfication;
}

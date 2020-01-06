package com.wlf.order.api.javabean;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 订单信息
 * @author wulinfeng
 * @since  2019/12/13
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Order implements Serializable {

    // 订单Id
    private String orderId;

    // 用户手机号
    private String servernum;

    // 地区
    private String area;

    // 产品列表
    private List<Product> products;

    // 收件人名称
    private String name;

    // 收件人电话
    private String linkphone;

    // 收件人地址
    private String address;

    // 合作方编码
    private String accessChannel;

    // 受理时间:YYYYMMDDHH24MMSS
    private String oprtime;

    // 受理渠道
    private String channel;

    // 操作员工号
    private String oprcode;
}

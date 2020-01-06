package com.wlf.order.prize.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * 订单表
 *
 * @author wulinfeng
 * @since 2019/12/13
 */
@Entity
@Table(name = "t_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 订单Id
    @Column(nullable = false,length = 32,unique = true)
    private String orderId;

    // 用户手机号
    @Column(length = 32)
    private String servernum;

    // 地市编码
    @Column(length = 32)
    private String area;

    // 收件人名称
    @Column(length = 32)
    private String name;

    // 收件人电话
    @Column(length = 32)
    private String linkphone;

    // 收件人地址
    @Column(length = 512)
    private String address;

    // 合作方编码
    @Column(length = 32)
    private String accessChannel;

    // 受理时间:YYYYMMDDHH24MMSS
    @Column(length = 32)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String oprtime;

    // 受理渠道
    @Column(length = 50)
    private String channel;

    // 操作员工号
    @Column(length = 32)
    private String oprcode;

    // 时间戳
    @Column(length = 13)
    private Long timestamp;
}

package com.wlf.order.prize.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "t_logistics")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 订单Id
    @Column(nullable = false,length = 32, unique = true)
    private String orderId;

    // 用户手机号
    @Column(length = 20)
    private String serviceNumber;

    // 物流单号
    @Column(length = 128)
    private String logisticsOrderNumber;

    // 物流公司名称
    @Column(length = 512)
    private String logisticsCompanyName;

    // 物流状态：0-未发货；1-发货中；2-已收货；3-已拒收
    @Column(length = 5)
    private String logisticsStatus;

    // 装维状态：0-未安装；1-已安装
    @Column(length = 5)
    private String installStatus;

    // 满意度
    @Column(length = 32)
    private String satisfication;

    // 时间戳
    @Column(length = 13)
    private Long timestamp;

}

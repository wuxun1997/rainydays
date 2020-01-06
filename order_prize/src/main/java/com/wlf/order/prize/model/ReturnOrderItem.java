package com.wlf.order.prize.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * 退订产品表
 *
 * @author wulinfeng
 * @since 2019/12/13
 */
@Entity
@Table(name = "t_returnorder", uniqueConstraints = @UniqueConstraint(columnNames = {"orderId", "productid"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 订单Id
    @Column(nullable = false, length = 32)
    private String orderId;

    // 用户手机号
    @Column(length = 32)
    private String servernum;

    // 产品编号
    @Column(length = 32)
    private String productid;

    // 产品组，默认0
    @Column(length = 5)
    private String productgroup;

    // 产品类别，默认0
    @Column(length = 5)
    private String producttype;

    // 产品名称
    @Column(length = 32)
    private String productname;

    // 订购类型：3-退订
    @Column(length = 5)
    private String ordertype;

    // 时间戳
    @Column(length = 13)
    private Long timestamp;
}

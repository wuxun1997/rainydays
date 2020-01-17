package com.wlf.order.prize.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * 产品表
 *
 * @author wulinfeng
 * @since 2019/12/13
 */
@Entity
@Table(name = "t_product", uniqueConstraints = @UniqueConstraint(columnNames = {"orderId", "productId"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 订单Id
    @Column(nullable = false, length = 32)
    private String orderId;

    // 受理产品编码
    @Column(length = 32)
    private String productId;

    // 产品名称
    @Column(length = 32)
    private String productName;

    // 产品设备号
    @Column(nullable = false, length = 128)
    private String equipmentId;

    // 时间戳
    @Column(length = 13)
    private Long timestamp;
}

package com.wlf.order.prize.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "t_infosource")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoSourceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 时间戳
    @Column(length = 13)
    private Long timestamp;

    // 序列号
    @Column(length = 32)
    private String requestSeq;

    // 订单编号
    @Column(length = 32)
    private String orderId;

    // 手机号码
    @Column(length = 32)
    private String userId;

    // 活动ID
    @Column(length = 32)
    private String activityId;

    // 通知结果
    @Column(length = 20)
    private String resultCode;

    // 结果信息
    @Column(length = 512)
    private String errorMsg;

    // 失败次数
    @Column(length = 5)
    private Integer failedCount;
}

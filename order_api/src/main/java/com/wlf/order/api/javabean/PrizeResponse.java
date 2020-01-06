package com.wlf.order.api.javabean;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PrizeResponse {

    // 32位流水，唯一
    private String requestSeq;

    // 应答结果码
    private String resultCode;

    // 应答结果信息
    private String resultMsg;

    // 用户Id(唯一标识)：手机号
    private String userId;

    // 活动Id
    private String activityId;

    // 活动时长（天）
    private String activityTime;

    // 领取时间
    private String receiveTime;

    // 到期时间
    private String invalidTime;

    // 产品名称
    private String productName;
}

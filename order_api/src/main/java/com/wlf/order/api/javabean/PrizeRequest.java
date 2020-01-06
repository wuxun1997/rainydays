package com.wlf.order.api.javabean;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PrizeRequest {

    // 32位流水，唯一
    private String requestSeq;

    // APPID编号
    private String appId;

    // APPID密码
    private String password;

    // 设备编号
    private String equipmentId;

    // 用户标识(用户手机号码) 或设备编号
    private String userId;

    // 活动Id
    private String activityId;
}

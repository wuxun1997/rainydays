package com.wlf.order.api.javabean;

import lombok.*;

/**
 * 结果信息
 * @author wulinfeng
 * @since  2019/12/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Result {

    // 结果码
    private int code;

    // 结果码描述
    private String msg;
}

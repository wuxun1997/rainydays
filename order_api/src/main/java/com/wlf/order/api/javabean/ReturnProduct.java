package com.wlf.order.api.javabean;

import lombok.*;

/**
 * 退货产品信息
 * @author wulinfeng
 * @since  2019/12/16
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReturnProduct {

    // 产品编号
    private String productid;

    // 产品组，默认0
    private String productgroup;

    // 产品类别，默认0
    private String producttype;

    // 产品名称
    private String productname;

    // 订购类型：2-退订, 3-退订完成
    private String ordertype;

}

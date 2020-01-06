package com.wlf.order.api.javabean;

import lombok.*;

/**
 * 产品信息
 * @author wulinfeng
 * @since  2019/12/16
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {

    // 受理产品编码
    private String productid;

    // 产品名称
    private String productname;

}

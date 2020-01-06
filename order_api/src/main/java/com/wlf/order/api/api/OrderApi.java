package com.wlf.order.api.api;

import com.wlf.order.api.javabean.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单接口
 *
 * @author wulinfeng
 * @since 2019/12/13
 */
@RequestMapping(value = "/jpservice")
public interface OrderApi {

    /**
     * 提供给ng录单：批量订单同步
     *
     * @param timestamp
     * @param sign
     * @param orders
     * @return
     */
    @RequestMapping("/ng/orders")
    Result syncronizeOrders(@RequestHeader Long timestamp,
                            @RequestHeader String sign,
                            @RequestBody Orders orders);

    /**
     * 提供给ng/点壹更新出库状态：同步订单状态
     *
     * @param timestamp
     * @param sign
     * @param logisticsOrders
     * @return
     */
    @RequestMapping(value = "/ng/order/status")
    Result syncronizeLogistics(@RequestHeader Long timestamp,
                               @RequestHeader String sign,
                               @RequestBody LogisticsOrders logisticsOrders);

    /**
     * 订单地址修改
     *
     * @param timestamp
     * @param sign
     * @param orderAddress
     * @return
     */
    @RequestMapping(value = "/ng/order/address")
    Result modifyAddress(@RequestHeader Long timestamp,
                         @RequestHeader String sign,
                         @RequestBody OrderAddress orderAddress);

    /**
     * 提供给ng退货同步
     *
     * @param timestamp
     * @param sign
     * @param returnOrder
     * @return
     */
    @RequestMapping(value = "/order/status")
    Result returnOrder(@RequestHeader Long timestamp,
                       @RequestHeader String sign,
                       @RequestBody ReturnOrder returnOrder);

    /**
     * 获取token
     *
     * @param url
     * @param timestamp
     * @param arg
     * @return
     */
    @RequestMapping(value = "/getSign")
    String getSign(@RequestParam String url,
                   @RequestParam String timestamp,
                   @RequestParam String arg,
                   @RequestParam String apiSecret);

    /**
     * 获取时间戳
     *
     * @param date
     * @return
     */
    @RequestMapping(value = "/getTimeStamp")
    String getTimeStamp(@RequestParam(required = false) String date);
}

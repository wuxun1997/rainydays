package com.wlf.order.stub.api;

import com.wlf.order.api.javabean.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 订购调用第三方的测试桩
 * @author wulinfeng
 * @since  2020/1/6
 */
public interface OrderStubApi {

    /**
     * NG提供给我方的出库通知
     *
     * @param sign
     * @param token
     * @param logisticsOrders
     * @return
     */
    @RequestMapping("sale/ng/servicesale/sale/province/order/notify")
    Result syncronizeOrderStatus(@RequestHeader String sign,
                                 @RequestHeader String token,
                                 @RequestBody LogisticsOrders logisticsOrders);

    /**
     * NG提供给我方退货接口
     *
     * @param sign
     * @param token
     * @param returnOrder
     * @return
     */
    @RequestMapping(value = "sale/ng/servicesale/sale/province/order/return/notify")
    Result returnOrder(@RequestHeader String sign,
                       @RequestHeader String token,
                       @RequestBody ReturnOrder returnOrder);

    /**
     * 点壹提供给我方：出库订单同步
     *
     * @param orders
     * @return
     */
    @RequestMapping("/order/my/status")
    Result syncronizeOrders(@RequestHeader Long timestamp,
                            @RequestHeader String sign,
                            @RequestBody Orders orders);

    /**
     * 点壹提供给我方：退货同步
     *
     * @param returnOrder
     * @return
     */
    @RequestMapping(value = "/order/status")
    Result returnOrder(@RequestHeader Long timestamp,
                       @RequestHeader String sign,
                       @RequestBody ReturnOrder returnOrder);

    /**
     * 开通权益
     *
     * @param prizeRequest
     * @return
     */
    @RequestMapping(value = "/is/getUserCollectionActivities")
    PrizeResponse openPrize(@RequestBody PrizeRequest prizeRequest);
}

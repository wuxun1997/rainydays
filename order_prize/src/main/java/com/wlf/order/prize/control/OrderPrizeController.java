package com.wlf.order.prize.control;

import com.wlf.order.api.api.OrderApi;
import com.wlf.order.api.util.OrderErrorCode;
import com.wlf.order.prize.exception.OrderException;
import com.wlf.order.api.javabean.*;
import com.wlf.order.prize.service.OrderService;
import com.wlf.order.prize.util.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 服务控制类
 *
 * @author wulinfeng
 * @since 2019/12/13
 */
@Slf4j
@RestController
public class OrderPrizeController implements OrderApi {

    private final static DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("${my_apiSecret}")
    private String my_apiSecret;

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public Result syncronizeOrders(Long timestamp, String sign, Orders orders) {

        log.info("syncronizeOrders begin, timestamp: {}, sign: {}, orders : {}",
                timestamp, sign, orders);
        Result result = new Result(OrderErrorCode.SUCCESS.getCode(), OrderErrorCode.SUCCESS.getMsg());
        try {
            // 鉴权
            orderService.signAuth(getUrl(), timestamp, sign, String.valueOf(orders.getOrderCount()), my_apiSecret);

            // 数据库操作
            orderService.insertOrders(orders, timestamp);
        } catch (OrderException e) {
            result.setCode(e.getCode());
            result.setMsg(e.getMsg());
        }

        // 记录话单业务数据
        Map<String, String> strMap = ThreadLocalUtil.getMap();
        strMap.put("apiType", "订单录入接口");
        strMap.put("biz", String.valueOf(orders.getOrderCount()));
        strMap.put("resultCode", String.valueOf(result.getCode()));

        log.info("syncronizeOrders end, result : {}", result);

        return result;
    }

    @Override
    public Result syncronizeLogistics(Long timestamp, String sign, LogisticsOrders logisticsOrders) {

        log.info("syncronizeLogistics timestamp: {}, sign: {}, begin, logisticsOrders : {}",
                timestamp, sign, logisticsOrders);

        Result result = new Result(OrderErrorCode.SUCCESS.getCode(), OrderErrorCode.SUCCESS.getMsg());
        try {
            // 鉴权
            orderService.signAuth(getUrl(), timestamp, sign, String.valueOf(logisticsOrders.getOrderCount()), my_apiSecret);

            // 更新出库信息
            orderService.updateOrderStatus(logisticsOrders, timestamp);
        } catch (OrderException e) {
            result.setCode(e.getCode());
            result.setMsg(e.getMsg());
        }

        // 记录话单业务数据
        Map<String, String> strMap = ThreadLocalUtil.getMap();
        strMap.put("apiType", "申请出货/订单状态变更接口");
        strMap.put("biz", String.valueOf(logisticsOrders.getOrderCount()));
        strMap.put("resultCode", String.valueOf(result.getCode()));

        log.info("syncronizeLogistics end, result : {}", result);

        return result;
    }

    @Override
    public Result returnOrder(Long timestamp, String sign, ReturnOrder returnOrder) {

        log.info("returnOrder begin, timestamp: {}, sign: {}, logisticsOrders : {}",
                timestamp, sign, returnOrder);

        Result result = new Result(OrderErrorCode.SUCCESS.getCode(), OrderErrorCode.SUCCESS.getMsg());
        try {
            orderService.signAuth(getUrl(), timestamp, sign, returnOrder.getOrderId(), my_apiSecret);
            orderService.insertReturnOrder(returnOrder, timestamp);
        } catch (OrderException e) {
            result.setCode(e.getCode());
            result.setMsg(e.getMsg());
        }

        // 记录话单业务数据
        Map<String, String> strMap = ThreadLocalUtil.getMap();
        strMap.put("apiType", "申请退货/完成退货接口");
        strMap.put("biz", String.valueOf(returnOrder.getOrderId()));
        strMap.put("resultCode", String.valueOf(result.getCode()));

        log.info("returnOrder end, result : {}", result);

        return result;
    }

    @Override
    public Result modifyAddress(Long timestamp, String sign, OrderAddress orderAddress) {

        log.info("modifyAddress begin, timestamp: {}, sign: {}, orderAddress : {}",
                timestamp, sign, orderAddress);

        Result result = new Result(OrderErrorCode.SUCCESS.getCode(), OrderErrorCode.SUCCESS.getMsg());
        try {
            orderService.signAuth(getUrl(), timestamp, sign, orderAddress.getOrderId(), my_apiSecret);
            orderService.modifyOrderAddress(orderAddress, timestamp);
        } catch (OrderException e) {
            result.setCode(e.getCode());
            result.setMsg(e.getMsg());
        }

        // 记录话单业务数据
        Map<String, String> strMap = ThreadLocalUtil.getMap();
        strMap.put("apiType", "订单地址修改接口");
        strMap.put("biz", String.valueOf(orderAddress.getOrderId()));
        strMap.put("resultCode", String.valueOf(result.getCode()));

        log.info("modifyAddress end, result : {}", result);

        return result;
    }

    @Override
    public String getSign(String url, String timestamp, String arg, String apiSecret) {
        String result;
        String seed = url + timestamp + arg + apiSecret;

        try {
            result = orderService.getSign(seed);
        } catch (OrderException e) {
            result = e.getMsg();
        }

        return result;
    }

    @Override
    public String getTimeStamp(String date) {
        if (date == null) {
            return String.valueOf(System.currentTimeMillis());
        }

        Date currentDate = null;
        try {
            currentDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return "your date format is error.";
        }

        return String.valueOf(currentDate.getTime());
    }

    /**
     * 获取当前请求的url
     *
     * @return
     */
    private String getUrl() {
        String url = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName()
                + ":" + httpServletRequest.getServerPort() + httpServletRequest.getServletPath();
        return url;
    }

}

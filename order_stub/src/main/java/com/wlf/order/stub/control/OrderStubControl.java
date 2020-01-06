package com.wlf.order.stub.control;

import com.wlf.order.api.javabean.*;
import com.wlf.order.stub.api.OrderStubApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@RestController
public class OrderStubControl implements OrderStubApi {

    private final static DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Result returnOrder(String sign, String token, ReturnOrder returnOrder) {
        log.info("request header: sign = {}, token = {}", sign, token);
        log.info("request body: logisticsOrders = {}", returnOrder);
        Result result = new Result(0, "success");
        return result;
    }

    @Override
    public Result syncronizeOrderStatus(String sign, String token, LogisticsOrders logisticsOrders) {
        log.info("request header: sign = {}, token = {}", sign, token);
        log.info("request body: logisticsOrders = {}", logisticsOrders);
        Result result = new Result(0, "success");
        return result;
    }

    @Override
    public Result syncronizeOrders(Long timestamp, String sign, Orders orders) {
        log.info("request header: timestamp = {}, sign = {}", timestamp, sign);
        log.info("request body: orders = {}", orders);
        Result result = new Result(0, "success");
        return result;
    }

    @Override
    public Result returnOrder(Long timestamp, String sign, ReturnOrder returnOrder) {
        log.info("request header: timestamp = {}, sign = {}", timestamp, sign);
        log.info("request body: orders = {}", returnOrder);
        Result result = new Result(0, "success");
        return result;
    }

    @Override
    public PrizeResponse openPrize(PrizeRequest prizeRequest) {
        Date now = new Date();
        String toDay = simpleDateFormat.format(now);
        String expireDay = simpleDateFormat.format(addDay(now, 60));
        log.info("request body: orders = {}", prizeRequest);
        PrizeResponse result = new PrizeResponse(prizeRequest.getRequestSeq(), "00000",
                "ok", prizeRequest.getUserId(), prizeRequest.getActivityId(), "60",
                toDay, expireDay, "10 元包");
        return result;
    }

    /**
     * 按激活时间计算失效时间
     *
     * @param toDay
     * @param activityTime
     * @return
     */
    private Date addDay(Date toDay, int activityTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDay);
        calendar.add(Calendar.DATE, activityTime);
        return calendar.getTime();
    }

}
